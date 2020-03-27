import React, { createContext, useEffect, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import OrgUserListDataSet from './OrgUserListDataSet';
import OrgRoleDataSet from './OrgRoleDataSet';
import OrgUserCreateDataSet from './OrgUserCreateDataSet';
import OrgUserRoleDataSet from './OrgUserRoleDataSet';
import PasswordPolicyDataSet from '../../../safe/org-safe/store/PasswordPolicyDataSet';
import OrgAllRoleDataSet from './OrgAllRoleDataSet';
import useStore from './useStore';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.user';
    const statusOptionData = [
      { text: '启用', value: 'true' },
      { text: '停用', value: 'false' },
    ];
    const statusOptionDs = useMemo(() => new DataSet({
      data: statusOptionData,
      selection: 'single',
    }));
    const safeOptionData = [
      { text: '正常', value: 'false' },
      { text: '锁定', value: 'true' },
    ];
    const safeOptionDs = useMemo(() => new DataSet({
      data: safeOptionData,
      selection: 'single',
    }));
    const orgRoleDataSet = useMemo(() => new DataSet(OrgRoleDataSet({ id, intl, intlPrefix })), [id]);
    const orgUserListDataSet = useMemo(() => new DataSet(OrgUserListDataSet({ id, intl, intlPrefix, statusOptionDs, safeOptionDs, orgRoleDataSet })), [id]);
    const orgUserCreateDataSet = useMemo(() => new DataSet(OrgUserCreateDataSet({ id, intl, intlPrefix, orgRoleDataSet })), [id]);
    const orgUserRoleDataSet = useMemo(() => new DataSet(OrgUserRoleDataSet({ id, intl, intlPrefix, orgRoleDataSet })), [id]);
    const passwordPolicyDataSet = useMemo(() => new DataSet(PasswordPolicyDataSet(id, id, intl, intlPrefix)), [id]);
    const orgAllRoleDataSet = useMemo(() => new DataSet(OrgAllRoleDataSet({ id, intl })), [id]);
    const userStore = useStore();

    useEffect(() => {
      userStore.checkCreate(organizationId);
    }, [organizationId]);
    
    const value = {
      ...props,
      orgUserListDataSet,
      orgUserCreateDataSet,
      orgUserRoleDataSet,
      orgRoleDataSet,
      orgAllRoleDataSet,
      prefixCls: 'base-org-user-list',
      intlPrefix,
      permissions: [
        'base-service.organization-user.pagingQueryUsersWithRolesOnOrganizationLevel', // 查询 0
        'base-service.organization-user.update', // 修改用户 1
        'base-service.organization-user.unlock', // 解锁 2
        'base-service.organization-user.resetUserPassword', // 重置密码 3
        'base-service.organization-user.enableUser', // 启用用户 4
        'base-service.organization-user.disableUser', // 禁用用户 5
        'base-service.organization-user.importUsersFromExcel', // 从excel里面批量导入用户 6
        'base-service.role-member.assignUsersRolesOnOrganizationLevel', // 组织层批量分配用户角色 7
        'base-service.role-member.updateUserRolesOnOrganizationLevel', // 组织层更新用户角色 8
        'base-service.organization.pagingQueryUsersOnOrganization', // 分页模糊查询组织下的用户 9
        'base-service.organization-user.latestHistory', // 查询最新的导入历史 10
        'base-service.organization-user.createUserWithRoles', // 创建用户并分配角色 11
      ],
      organizationId,
      passwordPolicyDataSet,
      userStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
