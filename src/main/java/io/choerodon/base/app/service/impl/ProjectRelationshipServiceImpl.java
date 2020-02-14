package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.utils.SagaTopic.ProjectRelationship.PROJECT_RELATIONSHIP_ADD;
import static io.choerodon.base.infra.utils.SagaTopic.ProjectRelationship.PROJECT_RELATIONSHIP_DELETE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import io.choerodon.base.api.vo.ProjectUserVO;
import io.choerodon.base.api.vo.UserVO;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.infra.mapper.ProjectCategoryMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.payload.ProjectRelationshipInsertPayload;
import io.choerodon.base.app.service.OrganizationProjectService;
import io.choerodon.base.app.service.ProjectRelationshipService;
import io.choerodon.base.infra.asserts.ProjectAssertHelper;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.base.infra.dto.ProjectRelationshipDTO;
import io.choerodon.base.infra.enums.ProjectCategory;
import io.choerodon.base.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.base.infra.mapper.ProjectRelationshipMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author Eugen
 */
@Service
public class ProjectRelationshipServiceImpl implements ProjectRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRelationshipServiceImpl.class);
    private static final String PROGRAM_CANNOT_BE_CONFIGURA_SUBPROJECTS = "error.program.cannot.be.configured.subprojects";
    private static final String AGILE_CANNOT_CONFIGURA_SUBPROJECTS = "error.agile.projects.cannot.configure.subprojects";
    private static final String RELATIONSHIP_NOT_EXIST_EXCEPTION = "error.project.relationship.not.exist";

    private TransactionalProducer producer;
    private ProjectRelationshipMapper relationshipMapper;
    private ProjectCategoryMapper projectCategoryMapper;
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    private OrganizationProjectService organizationProjectService;

    @Value("${choerodon.category.enabled:false}")
    private Boolean categoryEnable;

    private ProjectRelationshipMapper projectRelationshipMapper;

    private ProjectAssertHelper projectAssertHelper;

    private RoleMemberService roleMemberService;

    public ProjectRelationshipServiceImpl(TransactionalProducer producer, ProjectRelationshipMapper relationshipMapper,
                                          ProjectCategoryMapper projectCategoryMapper, ProjectMapCategoryMapper projectMapCategoryMapper,
                                          OrganizationProjectService organizationProjectService,
                                          ProjectRelationshipMapper projectRelationshipMapper,
                                          RoleMemberService roleMemberService,
                                          ProjectAssertHelper projectAssertHelper) {
        this.producer = producer;
        this.relationshipMapper = relationshipMapper;
        this.roleMemberService = roleMemberService;
        this.projectCategoryMapper = projectCategoryMapper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.organizationProjectService = organizationProjectService;
        this.projectRelationshipMapper = projectRelationshipMapper;
        this.projectAssertHelper = projectAssertHelper;
    }

    @Override
    public List<ProjectRelationshipDTO> getProjUnderGroup(Long projectId, Boolean onlySelectEnable) {
        ProjectDTO projectDTO;
        if (categoryEnable) {
            projectDTO = organizationProjectService.selectCategoryByPrimaryKey(projectId);
        } else {
            projectDTO = projectAssertHelper.projectNotExisted(projectId);
        }
        if (!projectDTO.getCategory().equalsIgnoreCase(ProjectCategory.PROGRAM.value()) &&
                !projectDTO.getCategory().equalsIgnoreCase(ProjectCategory.ANALYTICAL.value())) {
            throw new CommonException(AGILE_CANNOT_CONFIGURA_SUBPROJECTS);
        }
        List<ProjectRelationshipDTO> projectRelationshipDTOS = projectRelationshipMapper.selectProjectsByParentId(projectId, onlySelectEnable);
        handleProjectMember(projectRelationshipDTOS);
        return projectRelationshipDTOS;
    }

    @Override
    @Saga(code = PROJECT_RELATIONSHIP_DELETE, description = "项目群下移除项目", inputSchemaClass = ProjectRelationshipInsertPayload.class)
    public void removesAProjUnderGroup(Long orgId, Long groupId) {
        ProjectRelationshipDTO projectRelationshipDTO = projectRelationshipMapper.selectByPrimaryKey(groupId);
        if (projectRelationshipDTO == null) {
            throw new CommonException(RELATIONSHIP_NOT_EXIST_EXCEPTION);
        }
        if (categoryEnable && projectRelationshipDTO.getEnabled()) {
            removeProgramProject(projectRelationshipDTO.getProjectId());
        }
        ProjectRelationshipInsertPayload sagaPayload = new ProjectRelationshipInsertPayload();
        ProjectDTO parent = projectAssertHelper.projectNotExisted(projectRelationshipDTO.getParentId());
        sagaPayload.setCategory(parent.getCategory());
        sagaPayload.setParentCode(parent.getCode());
        sagaPayload.setParentId(parent.getId());
        ProjectDTO project = projectAssertHelper.projectNotExisted(projectRelationshipDTO.getProjectId());
        ProjectRelationshipInsertPayload.ProjectRelationship relationship
                = new ProjectRelationshipInsertPayload.ProjectRelationship(project.getId(), project.getCode(),
                projectRelationshipDTO.getStartDate(), projectRelationshipDTO.getEndDate(), projectRelationshipDTO.getEnabled(), BaseDTO.STATUS_DELETE);
        sagaPayload.setRelationships(Collections.singletonList(relationship));
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("organization")
                        .withSagaCode(PROJECT_RELATIONSHIP_DELETE),
                builder -> {
                    if (projectRelationshipMapper.selectByPrimaryKey(groupId) == null) {
                        throw new CommonException("error.delete.project.group.not.exist");
                    }
                    if (projectRelationshipMapper.deleteByPrimaryKey(groupId) != 1) {
                        throw new CommonException("error.delete.project.group");
                    }
                    builder
                            .withPayloadAndSerialize(sagaPayload)
                            .withRefId(String.valueOf(orgId))
                            .withSourceId(orgId);
                    return sagaPayload;
                });
    }

    @Saga(code = PROJECT_RELATIONSHIP_ADD, description = "iam组合项目中新增子项目", inputSchemaClass = ProjectRelationshipInsertPayload.class)
    @Override
    @Transactional
    public List<ProjectRelationshipDTO> batchUpdateRelationShipUnderProgram(Long orgId, List<ProjectRelationshipDTO> list) {
        //check list
        if (CollectionUtils.isEmpty(list)) {
            logger.info("The array for batch update relationships cannot be empty");
            return Collections.emptyList();
        }
        checkUpdate(list);
        //update与create分区
        List<ProjectRelationshipDTO> updateNewList = new ArrayList<>();
        List<ProjectRelationshipDTO> insertNewList = new ArrayList<>();
        list.forEach(g -> {
            if (g.getId() == null) {
                insertNewList.add(g);
            } else {
                updateNewList.add(g);
            }
        });
        List<ProjectRelationshipDTO> returnList = new ArrayList<>();
        // build project relationship saga payload
        ProjectRelationshipInsertPayload sagaPayload = new ProjectRelationshipInsertPayload();
        ProjectDTO parent = projectAssertHelper.projectNotExisted(list.get(0).getParentId());
        sagaPayload.setCategory(parent.getCategory());
        sagaPayload.setParentCode(parent.getCode());
        sagaPayload.setParentId(parent.getId());
        List<ProjectRelationshipInsertPayload.ProjectRelationship> relationships = new ArrayList<>();
        //批量插入
        insertNewList.forEach(relationshipDTO -> {
            checkGroupIsLegal(relationshipDTO);
            checkCategoryEnable(relationshipDTO);
            // insert
            if (projectRelationshipMapper.insertSelective(relationshipDTO) != 1) {
                throw new CommonException("error.create.project.group");
            }
            BeanUtils.copyProperties(projectRelationshipMapper.selectByPrimaryKey(relationshipDTO.getId()), relationshipDTO);
            returnList.add(relationshipDTO);
            if (categoryEnable && relationshipDTO.getEnabled()) {
                addProgramProject(relationshipDTO.getProjectId());
            }
            // fill the saga payload
            ProjectDTO project = projectAssertHelper.projectNotExisted(relationshipDTO.getProjectId());
            ProjectRelationshipInsertPayload.ProjectRelationship relationship
                    = new ProjectRelationshipInsertPayload.ProjectRelationship(project.getId(), project.getCode(),
                    relationshipDTO.getStartDate(), relationshipDTO.getEndDate(), relationshipDTO.getEnabled(), BaseDTO.STATUS_ADD);
            relationships.add(relationship);
        });
        //批量更新
        updateNewList.forEach(relationshipDTO -> {
            checkGroupIsLegal(relationshipDTO);
            // 更新项目群关系的有效结束时间
            updateProjectRelationshipEndDate(relationshipDTO);
            if (projectRelationshipMapper.selectByPrimaryKey(relationshipDTO.getId()) == null) {
                logger.warn("Batch update project relationship exists Nonexistent relationship,id is{}:{}", relationshipDTO.getId(), relationshipDTO);
            } else {
                checkCategoryEnable(relationshipDTO);
                ProjectRelationshipDTO projectRelationship = new ProjectRelationshipDTO();
                BeanUtils.copyProperties(relationshipDTO, projectRelationship);
                // update
                if (projectRelationshipMapper.updateByPrimaryKeySelective(projectRelationship) != 1) {
                    throw new CommonException("error.project.group.update");
                }
                projectRelationship = projectRelationshipMapper.selectByPrimaryKey(projectRelationship.getId());
                BeanUtils.copyProperties(projectRelationship, relationshipDTO);
                returnList.add(relationshipDTO);
                if (categoryEnable) {
                    if (relationshipDTO.getEnabled()) {
                        addProgramProject(relationshipDTO.getProjectId());
                    } else {
                        removeProgramProject(relationshipDTO.getProjectId());
                    }
                }
                // fill the saga payload
                ProjectDTO project =
                        projectAssertHelper.projectNotExisted(relationshipDTO.getProjectId());
                ProjectRelationshipInsertPayload.ProjectRelationship relationship
                        = new ProjectRelationshipInsertPayload.ProjectRelationship(project.getId(), project.getCode(),
                        relationshipDTO.getStartDate(), relationshipDTO.getEndDate(), relationshipDTO.getEnabled(), BaseDTO.STATUS_UPDATE);
                relationships.add(relationship);
            }
        });
        sagaPayload.setRelationships(relationships);
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("organization")
                        .withSagaCode(PROJECT_RELATIONSHIP_ADD),
                builder -> {
                    builder
                            .withPayloadAndSerialize(sagaPayload)
                            .withRefId(String.valueOf(orgId))
                            .withSourceId(orgId);
                    return sagaPayload;
                });
        return returnList;
    }


    private void addProgramProject(Long projectId) {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setCode("PROGRAM_PROJECT");
        projectCategoryDTO = projectCategoryMapper.selectOne(projectCategoryDTO);

        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setProjectId(projectId);
        projectMapCategoryDTO.setCategoryId(projectCategoryDTO.getId());

        if (projectMapCategoryMapper.insert(projectMapCategoryDTO) != 1) {
            throw new CommonException("error.project.map.category.insert");
        }
    }

    private void removeProgramProject(Long projectId) {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setCode("PROGRAM_PROJECT");
        projectCategoryDTO = projectCategoryMapper.selectOne(projectCategoryDTO);

        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setProjectId(projectId);
        projectMapCategoryDTO.setCategoryId(projectCategoryDTO.getId());

        if (projectMapCategoryMapper.delete(projectMapCategoryDTO) != 1) {
            throw new CommonException("error.project.map.category.delete");
        }
    }

    /**
     * 更新项目群关系的有效结束时间.
     *
     * @param projectRelationshipDTO 项目群关系
     */
    private void updateProjectRelationshipEndDate(ProjectRelationshipDTO projectRelationshipDTO) {
        // 启用操作 结束时间置为空
        if (projectRelationshipDTO.getEnabled()) {
            projectRelationshipDTO.setEndDate(null);
        } else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                // 禁用操作 结束时间为禁用操作的时间
                projectRelationshipDTO.setEndDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
            } catch (ParseException e) {
                logger.info("Relationship end time format failed");
            }
        }
    }

    private void checkCategoryEnable(ProjectRelationshipDTO relationshipDTO) {
        if (categoryEnable) {
            if (organizationProjectService.selectCategoryByPrimaryKey(relationshipDTO.getParentId()).getCategory()
                    .equalsIgnoreCase(ProjectCategory.PROGRAM.value())) {
                relationshipDTO.setProgramId(relationshipDTO.getParentId());
            }
        } else if (projectAssertHelper.projectNotExisted(relationshipDTO.getParentId()).getCategory()
                .equalsIgnoreCase(ProjectCategory.PROGRAM.value())) {
            relationshipDTO.setProgramId(relationshipDTO.getParentId());
        }
    }

    /**
     * 校验批量更新DTO
     * 检验不能为空
     * 校验不能批量更新不同项目群下的项目关系
     * 校验项目本身已停用 则无法被项目群添加或更新
     * 校验一个项目只能被一个普通项目群添加
     * 校验一个项目只能被一个普通项目群更新
     *
     * @param list 项目群关系列表
     */
    private void checkUpdate(List<ProjectRelationshipDTO> list) {
        // list不能为空
        if (list == null || list.isEmpty()) {
            logger.info("The array for batch update relationships cannot be empty");
            return;
        }
        list.forEach(r -> {
            // 开始时间为空则填充为当前时间
            if (r.getStartDate() == null) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    r.setStartDate(simpleDateFormat.parse(simpleDateFormat.format(new Date())));
                } catch (ParseException e) {
                    logger.info("Relationship start time format failed");
                }
            }

            // 项目已停用 无法被项目群添加或更新
            ProjectDTO project = projectAssertHelper.projectNotExisted(r.getProjectId());
            if (!project.getEnabled()) {
                throw new CommonException("error.insertOrUpdate.project.relationships.when.project.disabled", project.getName());
            }
            if (r.getId() == null) {
                // 一个项目只能被一个普通项目群添加
                List<ProjectDTO> projectDTOS = relationshipMapper.selectProgramsByProjectId(r.getProjectId(), true);
                if (projectDTOS != null && projectDTOS.size() > 0) {
                    throw new CommonException("error.insert.project.relationships.exists.one.program", projectDTOS.get(0).getName());
                }
            } else if (r.getEnabled()) {
                // 一个项目只能被一个普通项目群更新
                List<ProjectDTO> projectDTOS = relationshipMapper.selectProgramsByProjectId(r.getProjectId(), true);
                if (projectDTOS != null && projectDTOS.size() > 0) {
                    List<String> programs = new ArrayList<>();
                    for (ProjectDTO projectDTO : projectDTOS) {
                        programs.add(projectDTO.getName());
                    }
                    throw new CommonException("error.update.project.relationships.exists.multiple.program", StringUtils.join(programs, ","));
                }
            }
        });
        Set<Long> collect = list.stream().map(ProjectRelationshipDTO::getParentId).collect(Collectors.toSet());
        if (collect.size() != 1) {
            throw new CommonException("error.update.project.relationships.must.be.under.the.same.program");
        }
    }

    /**
     * 校验
     * 校验parent是否为空，是否为项目群
     * 校验project是否为空，是否为敏捷项目或普通应用项目
     */
    private void checkGroupIsLegal(ProjectRelationshipDTO projectRelationshipDTO) {
        ProjectDTO parent;
        if (categoryEnable) {
            parent = organizationProjectService.selectCategoryByPrimaryKey(projectRelationshipDTO.getParentId());
        } else {
            parent = projectAssertHelper.projectNotExisted(projectRelationshipDTO.getParentId());
        }
        if (!parent.getCategory().equalsIgnoreCase(ProjectCategory.PROGRAM.value()) &&
                !parent.getCategory().equalsIgnoreCase(ProjectCategory.ANALYTICAL.value())) {
            throw new CommonException(AGILE_CANNOT_CONFIGURA_SUBPROJECTS);
        }
        ProjectDTO son;
        if (categoryEnable) {
            son = organizationProjectService.selectCategoryByPrimaryKey(projectRelationshipDTO.getProjectId());
        } else {
            son = projectAssertHelper.projectNotExisted(projectRelationshipDTO.getProjectId());
        }
        String sonCategory = son.getCategory();
        if (!(ProjectCategory.AGILE.value().equalsIgnoreCase(sonCategory) || ProjectCategory.GENERAL.value().equalsIgnoreCase(sonCategory))) {
            throw new CommonException(PROGRAM_CANNOT_BE_CONFIGURA_SUBPROJECTS);
        }
    }
    private void handleProjectMember(List<ProjectRelationshipDTO> projectRelationshipDTOS){
        if (!CollectionUtils.isEmpty(projectRelationshipDTOS)) {
            List<Long> projectList = projectRelationshipDTOS.stream().map(ProjectRelationshipDTO::getProjectId).collect(Collectors.toList());
            List<ProjectUserVO> projectUserVOS = roleMemberService.queryMemberInProject(projectList);
            if (!CollectionUtils.isEmpty(projectList)) {
                Map<Long, List<UserVO>> longListMap = projectUserVOS.stream().collect(Collectors.toMap(ProjectUserVO::getProjectId, ProjectUserVO::getUserVOS));
                projectRelationshipDTOS.forEach(v -> {
                    v.setUserVOs(longListMap.get(v.getProjectId()));
                });
            }
        }
    }
}
