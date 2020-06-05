package io.choerodon.iam.app.task;

import java.util.*;
import java.util.stream.Collectors;

import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.RolePermission;
import org.hzero.iam.infra.constant.RolePermissionType;
import org.hzero.iam.infra.mapper.RolePermissionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.RolePermissionC7nMapper;
import io.choerodon.iam.infra.utils.C7nCollectionUtils;


/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  16:44 2019/3/11
 * Description:
 */
@Component
public class PermissionFixRunner implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionFixRunner.class);

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private RolePermissionC7nMapper rolePermissionC7nMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Value("${fix.data.page.size:200}")
    private Integer pageSize;

    @Value("${fix.data.page.sleep.time: 500}")
    private Integer sleepTime;

    @Override
    public void run(String... strings) {
        try {

            // 修复子角色权限（保持和模板角色权限一致）
            fixChildPermission();

        } catch (Exception e) {
            throw new CommonException("error.fix.role.permission.data", e);
        }

    }

    private void fixChildPermission() {
        // 查询模板角色
        List<Role> tplRoles = roleC7nMapper.listByLabelNames(0L, RoleLabelEnum.TENANT_ROLE_TPL.value());

        tplRoles.forEach(tplRole -> {
            // 查询模板角色拥有的权限
            List<RolePermission> tplPs = rolePermissionC7nMapper.listRolePermissionIds(tplRole.getId());
            Set<Long> tplPsIds = tplPs.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());

            // 查询模板子角色
            List<Role> childRoles = roleC7nMapper.listChildRoleByTplRoleId(tplRole.getId());

            // 修复子角色权限
            childRoles.forEach(childRole -> {
                List<RolePermission> childPs = rolePermissionC7nMapper.listRolePermissionIds(childRole.getId());
                Map<Long, RolePermission> childPsMap = childPs.stream().collect(Collectors.toMap(RolePermission::getPermissionSetId, v -> v));
                Set<Long> childPsIds = childPs.stream().map(RolePermission::getPermissionSetId).collect(Collectors.toSet());

                Set<Long> addPsIds = new HashSet<>();
                Set<Long> delPsIds = new HashSet<>();

                if (CollectionUtils.isEmpty(tplPsIds)) {
                    delPsIds = childPsIds;
                } else {
                    addPsIds = tplPsIds.stream().filter(id -> !childPsIds.contains(id)).collect(Collectors.toSet());
                    delPsIds = childPsIds.stream().filter(id -> !tplPsIds.contains(id)).collect(Collectors.toSet());
                }

                // 删除子角色权限
                if (!CollectionUtils.isEmpty(delPsIds)) {
                    // 要删除的role-permission-id
                    Set<Long> delRpIds = new HashSet<>();
                    delPsIds.forEach(id -> {
                        RolePermission rolePermission = childPsMap.get(id);
                        delRpIds.add(rolePermission.getId());
                    });
                    rolePermissionC7nMapper.batchDeleteById(delRpIds);
                }
                // 新增子角色权限
                List<RolePermission> rolePermissionList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(addPsIds)) {
                    addPsIds.forEach(id -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setCreateFlag("N");
                        rolePermission.setInheritFlag("Y");
                        rolePermission.setRoleId(childRole.getId());
                        rolePermission.setPermissionSetId(id);
                        rolePermission.setType(RolePermissionType.PS.name());
                        rolePermission.setCreationDate(new Date());
                        rolePermission.setCreatedBy(0L);
                        rolePermission.setLastUpdateDate(new Date());
                        rolePermission.setLastUpdatedBy(0L);
                        rolePermissionList.add(rolePermission);
                    });

                    if (rolePermissionList.size() > pageSize) {

                        List<List<RolePermission>> fragmentList = C7nCollectionUtils.fragmentList(rolePermissionList, pageSize);
                        for (List<RolePermission> permissions : fragmentList) {
                            rolePermissionC7nMapper.batchInsert(permissions);
                            try {
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        rolePermissionC7nMapper.batchInsert(rolePermissionList);
                    }
                }
            });
        });
    }

}
