package io.choerodon.iam.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.QuickLinkVO;
import io.choerodon.iam.app.service.QuickLinkService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.QuickLinkDTO;
import io.choerodon.iam.infra.enums.QuickLinkShareScopeEnum;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.ProjectUserMapper;
import io.choerodon.iam.infra.mapper.QuickLinkMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 16:13
 */
@Service
public class QuickLinkServiceImpl implements QuickLinkService {

    private static final String ERROR_SAVE_QUICK_LINK_FAILED = "error.save.quick.link.failed";
    private static final String ERROR_UPDATE_QUICK_LINK_FAILED = "error.update.quick.link.failed";
    private static final String ERROR_DELETE_QUICK_LINK_FAILED = "error.delete.quick.link.failed";
    private static final String ERROR_QUICK_LINK_NOT_FOUND = "error.quick.link.not.found";


    @Autowired
    private QuickLinkMapper quickLinkMapper;
    @Autowired
    private UserC7nService userC7nService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Override
    @Transactional
    public void create(QuickLinkDTO quickLinkDTO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Assert.notNull(userId, ResourceCheckConstants.ERROR_NOT_LOGIN);
        if (!QuickLinkShareScopeEnum.contains(quickLinkDTO.getScope())) {
            throw new CommonException(ResourceCheckConstants.ERROR_PARAM_IS_INVALID);
        }

        quickLinkDTO.setId(null);
        quickLinkDTO.setCreateUserId(userId);

        if (quickLinkMapper.insertSelective(quickLinkDTO) != 1) {
            throw new CommonException(ERROR_SAVE_QUICK_LINK_FAILED);
        }
    }

    @Override
    @Transactional
    public void update(Long id, QuickLinkDTO quickLinkDTO) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Assert.notNull(userId, ResourceCheckConstants.ERROR_NOT_LOGIN);
        Assert.notNull(id, ResourceCheckConstants.ERROR_TARGET_ID_IS_NULL);

        quickLinkDTO.setId(id);
        checkEditPermisison(id, userId);

        if (quickLinkMapper.updateByPrimaryKeySelective(quickLinkDTO) != 1) {
            throw new CommonException(ERROR_UPDATE_QUICK_LINK_FAILED);
        }

    }

    @Override
    @Transactional
    public void delete(Long id) {
        Assert.notNull(id, ResourceCheckConstants.ERROR_TARGET_ID_IS_NULL);
        if (quickLinkMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException(ERROR_DELETE_QUICK_LINK_FAILED);
        }
    }

    @Override
    public Page<QuickLinkVO> query(Long organizationId, Long projectId, PageRequest pageable) {
        Assert.notNull(organizationId, ResourceCheckConstants.ERROR_ORGANIZATION_ID_IS_NULL);
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Long userId = userDetails.getUserId();
        Assert.notNull(userId, ResourceCheckConstants.ERROR_NOT_LOGIN);
        Page<QuickLinkVO> page;
        if (Boolean.FALSE.equals(userDetails.getAdmin()) && Boolean.FALSE.equals(userC7nService.checkIsOrgRoot(organizationId, userId))) {
            List<ProjectDTO> projectDTOS = projectUserMapper.listOwnedProject(organizationId, userId);
            Set<Long> pIds;
            if (!CollectionUtils.isEmpty(projectDTOS)) {
                pIds = projectDTOS.stream().map(ProjectDTO::getId).collect(Collectors.toSet());
            } else {
                pIds = new HashSet<>();
            }
            page = PageHelper.doPage(pageable, () -> quickLinkMapper.queryByPids(projectId, userId, pIds));
        } else {
            page = PageHelper.doPage(pageable, () -> quickLinkMapper.queryAll(organizationId, projectId, userId));
        }



        List<QuickLinkVO> content = page.getContent();
        content.stream().filter(v -> QuickLinkShareScopeEnum.SELF.value().equals(v.getScope())).forEach(v -> {
            if (v.getCreateUserId() == userId) {
                v.setEditFlag(true);
            }
        });
        Set<Long> pids = projectMapper.listUserManagedProjectInOrg(organizationId, userId);
        content.stream().filter(v -> QuickLinkShareScopeEnum.PROJECT.value().equals(v.getScope())).forEach(v -> {
            if (pids.contains(v.getProjectId())) {
                v.setEditFlag(true);
            }
        });
        return page;
    }

    private void checkEditPermisison(Long id, Long userId) {
        QuickLinkDTO quickLinkDTO = quickLinkMapper.selectByPrimaryKey(id);
        if (quickLinkDTO == null) {
            throw new CommonException(ERROR_QUICK_LINK_NOT_FOUND);
        }

        if (QuickLinkShareScopeEnum.SELF.value().equals(quickLinkDTO.getScope())
                && !quickLinkDTO.getCreateUserId().equals(userId)) {
            throw new CommonException(ResourceCheckConstants.ERROR_PERMISION_CHECK_FAILED);
        } else if (QuickLinkShareScopeEnum.PROJECT.value().equals(quickLinkDTO.getScope())
                && !quickLinkDTO.getCreateUserId().equals(userId)
                && !userC7nService.checkIsProjectOwner(userId, quickLinkDTO.getProjectId())) {
            throw new CommonException(ResourceCheckConstants.ERROR_PERMISION_CHECK_FAILED);
        }
    }
}
