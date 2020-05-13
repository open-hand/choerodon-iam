import React, { createContext, useMemo, useContext } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import PermissionInfoDataSet from './PermissionInfoDataSet';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'MenuStore')(
  (props) => {
    const { AppState: { getUserInfo: { id }, menuType: { orgId } }, intl, children } = props;
    const intlPrefix = 'user.permissioninfo';
    const permissionInfoDataSet = useMemo(() => new DataSet(PermissionInfoDataSet(id, intl, `${intlPrefix}.table`, orgId)), []);
    const value = {
      ...props,
      prefixCls: 'user-permissioninfo',
      intlPrefix,
      permissions: [
        'base-service.user.uploadPhoto',
      ],
      permissionInfoDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
