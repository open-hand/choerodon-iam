import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import UserListDataSet from './UserListDataSet';
import OrgRoleDataSet from './OrgRoleDataSet';
import UserRoleDataSet from './UserRoleDataSet';
import OrganizationDataSet from './OrganizationDataSet';
import AllRoleDataSet from './AllRoleDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { intl, children } = props;
    const intlPrefix = 'global.user';
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
    const organizationDataSet = useMemo(() => new DataSet(OrganizationDataSet()));
    const orgRoleDataSet = useMemo(() => new DataSet(OrgRoleDataSet({ intl, intlPrefix })));
    const userListDataSet = useMemo(() => new DataSet(UserListDataSet({ intl, intlPrefix, statusOptionDs, safeOptionDs, orgRoleDataSet, organizationDataSet })));
    const userRoleDataSet = useMemo(() => new DataSet(UserRoleDataSet({ intl, intlPrefix, orgRoleDataSet })));
    const allRoleDataSet = useMemo(() => new DataSet(AllRoleDataSet()));
    const value = {
      ...props,
      userListDataSet,
      userRoleDataSet,
      allRoleDataSet,
      orgRoleDataSet,
      organizationDataSet,
      prefixCls: 'base-site-user-list',
      intlPrefix,
      permissions: [
        'base-service.role-member.listUsersOnSiteLevel', // 全局层查询启用状态的用户列表 0
      ],
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
