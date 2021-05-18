package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.api.vo.UserGuideVO;
import io.choerodon.iam.app.service.UserGuideService;
import io.choerodon.iam.infra.mapper.UserGuideMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/5/18 11:11
 */
@Service
public class UserGuideServiceImpl implements UserGuideService {

    @Autowired
    private UserGuideMapper userGuideMapper;

    @Override
    public List<UserGuideVO> listUserGuideByMenuId(Long menuId) {

        return userGuideMapper.listUserGuideByMenuId(menuId);
    }
}
