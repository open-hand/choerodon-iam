import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import LdapDataSet from './LdapDataSet';
import LdapTestDataSet from './LdapTestDataSet';
import OrganizationDataSet from './OrganizationDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const { children, AppState: { currentMenuType: { id: orgId, name } } } = props;
  const ldapDataSet = useMemo(() => new DataSet(LdapDataSet({ orgId, name })), [orgId]);
  const ldapTestDataSet = useMemo(() => new DataSet(LdapTestDataSet({ orgId })), [orgId]);
  const organizationDataSet = useMemo(() => new DataSet(OrganizationDataSet({ id: orgId })), [orgId]);
  const value = {
    ...props,
    orgId,
    orgName: name,
    ldapDataSet,
    ldapTestDataSet,
    organizationDataSet,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
