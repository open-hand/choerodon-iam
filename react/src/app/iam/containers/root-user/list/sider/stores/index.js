import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import AllUserDataSet from './AllUserDataSet';

const Store = createContext();

export default Store;

export const SiderStoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, context } = props;
    const intlPrefix = 'global.root-user.sider';
    const dsStore = [];
    const value = {
      ...props,
      ...context,
      AllUserDataSet,
      prefixCls: 'base-root-user-sider',
      intlPrefix,
      intl,
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
