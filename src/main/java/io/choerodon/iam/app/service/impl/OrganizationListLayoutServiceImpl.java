package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import io.choerodon.iam.api.vo.ListLayoutColumnRelVO;
import io.choerodon.iam.api.vo.ListLayoutVO;
import io.choerodon.iam.app.service.ListLayoutService;
import io.choerodon.iam.app.service.OrganizationListLayoutService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.dto.ProjectDTO;

/**
 * @author superlee
 * @since 2021-10-19
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrganizationListLayoutServiceImpl implements OrganizationListLayoutService {

    @Autowired
    private ProjectC7nService projectC7nService;

    @Autowired
    private ListLayoutService listLayoutService;

    @Override
    public ListLayoutVO queryByApplyType(Long organizationId, String applyType) {
        ListLayoutVO layout = listLayoutService.queryByApplyType(organizationId, 0L, applyType);
        if (ObjectUtils.isEmpty(layout)) {
            return null;
        }
        return layout;
    }

    @Override
    public ListLayoutVO save(Long organizationId, ListLayoutVO listLayoutVO) {
        return listLayoutService.save(organizationId, 0L, listLayoutVO);
    }
}
