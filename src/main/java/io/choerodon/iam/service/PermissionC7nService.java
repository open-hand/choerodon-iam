package io.choerodon.iam.service;

import java.util.List;

import org.hzero.iam.domain.entity.Permission;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
public interface PermissionC7nService {

    List<Permission> query(String level, String serviceName, String code);

}
