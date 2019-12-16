import React, { createContext, useContext, useEffect, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import useStore from './useStore';
import SyncRecordDataSet from './SyncRecordDataSet';
import SyncFormDataSet from './SyncFormDataSet';
import SyncRecordTableDataSet from './SyncRecordTableDataSet';

const Store = createContext();

export function useLdapStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const {
      AppState: { currentMenuType: { organizationId: orgId } },
      children,
    } = props;

    const ldapStore = useStore();
    const syncRecordDs = useMemo(() => new DataSet(SyncRecordDataSet({ orgId })), [orgId]);
    const syncFormDs = useMemo(() => new DataSet(SyncFormDataSet({ orgId })), [orgId]);
    const recordTableDs = useMemo(() => new DataSet(SyncRecordTableDataSet({ orgId })), [orgId]);

    useEffect(() => {
      if (!syncFormDs.current) {
        syncFormDs.create();
      }
    }, []);

    const value = {
      ...props,
      prefixCls: 'base-org-user-ldap',
      ldapStore,
      syncRecordDs,
      syncFormDs,
      recordTableDs,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
