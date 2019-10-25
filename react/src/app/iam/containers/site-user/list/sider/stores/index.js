import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import RoleAssignDataSet from './RoleAssignDataSet';

const Store = createContext();

export default Store;

export const SiderStoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { getUserId: userId }, intl, children } = props;
    const roleAssignDataSet = useMemo(() => new DataSet(RoleAssignDataSet({ intl })));
    const intlPrefix = 'global.user.sider';
    const dsStore = [];
    const value = {
      ...props,
      prefixCls: 'base-site-user-sider',
      intlPrefix,
      roleAssignDataSet,
      userId,
      dsStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
