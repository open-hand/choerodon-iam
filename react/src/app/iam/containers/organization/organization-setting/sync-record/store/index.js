import React, { createContext, useContext, useMemo, useCallback } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import synRecordDataState from './synRecordDataState';
import LDAPStore from '../../stores';
// import errorUserDataSet from './errorUserDataSet'

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const { ldapDataSet } = useContext(LDAPStore);
  const ldapId = ldapDataSet.current && ldapDataSet.current.get('id');
  const { children, AppState: { currentMenuType: { id: orgId, name } } } = props;
  const getLdapDataSet = useCallback(() => ldapDataSet.current && new DataSet(synRecordDataState({ orgId, ldapId: ldapDataSet.current.get('id') })));
  const value = {
    getLdapDataSet,
    history: props.history,
    orgName: name,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
