package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import groovy.lang.Lazy;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.StarProjectService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.constant.MisConstants;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.StarProjectUserRelDTO;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.StarProjectMapper;
import io.choerodon.iam.infra.utils.CommonExAssertUtil;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 10:25
 */
@Service
public class StarProjectServiceImpl implements StarProjectService {

    private static final String ERROR_NOT_LOGIN = "error.not.login";
    private static final String ERROR_PROJECT_IS_NULL = "error.project.is.null";
    private static final String ERROR_SAVE_STAR_PROJECT_FAILED = "error.save.star.project.failed";
    private static final String ERROR_DELETE_STAR_PROJECT_FAILED = "error.delete.star.project.failed";
    private static final String IAM = "iam";

    @Autowired
    private StarProjectMapper starProjectMapper;
    @Autowired
    private ProjectC7nService projectC7nService;
    @Autowired
    private UserC7nService userC7nService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private RedisHelper redisHelper;

    @Override
    @Transactional
    public void create(Long organizationId, StarProjectUserRelDTO starProjectUserRelDTO) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(starProjectUserRelDTO.getProjectId());
        CommonExAssertUtil.assertTrue(organizationId.equals(projectDTO.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        // 数据校验
        projectC7nService.checkNotExistAndGet(starProjectUserRelDTO.getProjectId());
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Assert.notNull(userId, ERROR_NOT_LOGIN);
        starProjectUserRelDTO.setUserId(userId);
        starProjectUserRelDTO.setOrganizationId(organizationId);
        Long sort = getstarProjectSort(organizationId, userId);
        starProjectUserRelDTO.setSort(sort);
        if (starProjectMapper.selectOne(starProjectUserRelDTO) == null) {
            if (starProjectMapper.insertSelective(starProjectUserRelDTO) > 1) {
                throw new CommonException(ERROR_SAVE_STAR_PROJECT_FAILED);
            }
        }
    }

    @Override
    public boolean isStarProject(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        StarProjectUserRelDTO starProjectUserRelDTO = new StarProjectUserRelDTO();
        starProjectUserRelDTO.setUserId(userId);
        starProjectUserRelDTO.setProjectId(projectId);
        return starProjectMapper.selectOne(starProjectUserRelDTO) != null;
    }

    @Override
    public List<Long> listStarProjectIds(Set<Long> projectIds) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return starProjectMapper.query(projectIds, userId).stream().map(ProjectDTO::getId).collect(Collectors.toList());
    }

    private synchronized Long getstarProjectSort(Long organizationId, Long userId) {
        Long dbMaxSeq = starProjectMapper.getDbMaxSeq(organizationId, userId);
        if (dbMaxSeq == null || dbMaxSeq == 0L) {
            return Long.valueOf(BaseConstants.Digital.ONE);
        }
        AtomicLong atomicLong = new AtomicLong(dbMaxSeq + 1);
        return atomicLong.getAndIncrement();
    }


    @Transactional
    @Override
    public void delete(Long projectId) {
        Assert.notNull(projectId, ERROR_PROJECT_IS_NULL);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Assert.notNull(userId, ERROR_NOT_LOGIN);

        StarProjectUserRelDTO record = new StarProjectUserRelDTO();
        record.setProjectId(projectId);
        record.setUserId(userId);
        StarProjectUserRelDTO starProjectUserRelDTO = starProjectMapper.selectOne(record);

        if (starProjectUserRelDTO != null) {
            if (starProjectMapper.deleteByPrimaryKey(starProjectUserRelDTO.getId()) > 1) {
                throw new CommonException(ERROR_DELETE_STAR_PROJECT_FAILED);
            }
        }

    }

    @Override
    public List<ProjectDTO> query(Long organizationId, Integer size) {
        Assert.notNull(organizationId, ResourceCheckConstants.ERROR_ORGANIZATION_ID_IS_NULL);
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long userId = userDetails.getUserId();
        Assert.notNull(userId, ResourceCheckConstants.ERROR_NOT_LOGIN);

        boolean isAdmin = Boolean.TRUE.equals(userDetails.getAdmin()) || Boolean.TRUE.equals(userC7nService.checkIsOrgRoot(organizationId, userId));
        List<ProjectDTO> projectDTOS = starProjectMapper.queryWithLimit(organizationId, userId, isAdmin, size);
        if (CollectionUtils.isEmpty(projectDTOS)) {
            return new ArrayList<>();
        }

        boolean isOrgAdmin = userC7nService.checkIsOrgRoot(organizationId, userId);
        List<Long> activeProjectIds = projectMapper.selectProjectsByUserIdOrAdmin(organizationId, userId, null, isAdmin, isOrgAdmin, null).stream().map(ProjectDTO::getId).collect(Collectors.toList());

        Set<Long> pids = projectDTOS.stream().filter(t->activeProjectIds.contains(t.getId())).map(ProjectDTO::getId).collect(Collectors.toSet());
        return starProjectMapper.query(pids, userId);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStarProject(Long organizationId, List<ProjectDTO> projectDTOS) {
        if (CollectionUtils.isEmpty(projectDTOS)) {
            return;
        }
        Long userId = DetailsHelper.getUserDetails().getUserId();

        StarProjectUserRelDTO starProjectUserRelDTO = new StarProjectUserRelDTO();
        starProjectUserRelDTO.setUserId(userId);
        starProjectUserRelDTO.setOrganizationId(organizationId);
        List<StarProjectUserRelDTO> starProjectUserRelDTOS = starProjectMapper.select(starProjectUserRelDTO);
        if (CollectionUtils.isEmpty(starProjectUserRelDTOS)) {
            return;
        }
        Map<Long, List<StarProjectUserRelDTO>> longListMap = starProjectUserRelDTOS.stream().collect(Collectors.groupingBy(StarProjectUserRelDTO::getProjectId));
        List<Long> projectIds = projectDTOS.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        AtomicLong index = new AtomicLong(1L);
        Collections.reverse(projectIds);
        projectIds.forEach(id -> {
            StarProjectUserRelDTO starProjectUserRelDTO1 = longListMap.get(id).get(0);
            starProjectUserRelDTO1.setSort(index.getAndIncrement());
            starProjectMapper.updateByPrimaryKeySelective(starProjectUserRelDTO1);
        });
    }
}
