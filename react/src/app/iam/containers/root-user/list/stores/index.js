import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import AdminListDataSet from './adminListDataSet';
import AdminCreateDataSet from './adminCreateDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'global.root-user.list';
    const adminListDataSet = useMemo(() => new DataSet(AdminListDataSet({ id, intl, intlPrefix })), [id]);
    const adminCreateDataSet = useMemo(() => new DataSet(AdminCreateDataSet({ id, organizationId, intl, intlPrefix })), [id]);
    const permissions = [
      'base-service.user.pagingQueryAdminUsers',
      'base-service.user.addDefaultUsers',
      'base-service.user.deleteDefaultUser',
    ];
    const value = {
      ...props,
      adminListDataSet,
      adminCreateDataSet,
      prefixCls: 'base-root-user-list',
      intlPrefix,
      organizationId,
      permissions,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
