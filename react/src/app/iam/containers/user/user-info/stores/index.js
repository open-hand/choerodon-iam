import React, { createContext, useMemo, useContext } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import UserInfoStoreObject from './UserInfoStore';

const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId }, getUserId: userId }, intl, children } = props;
    const intlPrefix = 'user.userinfo';
    const UserInfoStore = useMemo(() => new UserInfoStoreObject(), []);
    const value = {
      ...props,
      userId,
      prefixCls: 'user-info',
      intlPrefix,
      permissions: [
        'base-service.user.uploadPhoto',
      ],
      UserInfoStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
