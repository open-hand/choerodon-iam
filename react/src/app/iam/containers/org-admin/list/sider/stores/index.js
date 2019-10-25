import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { observable } from 'mobx';
import { injectIntl } from 'react-intl';
import OrgUserDataSet from './OrgUserDataSet';

const Store = createContext();

export default Store;

export const SiderStoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.admin.sider';
    const OrgUserDataSetConfig = useMemo(() => OrgUserDataSet({ id: organizationId, intl }), []);
    const dsStore = [];
    const value = {
      ...props,
      OrgUserDataSetConfig,
      prefixCls: 'base-org-admin-sider',
      intlPrefix,
      dsStore,
      organizationId,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
