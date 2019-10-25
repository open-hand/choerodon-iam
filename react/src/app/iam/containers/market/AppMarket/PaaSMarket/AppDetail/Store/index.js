import React, { createContext, useMemo, useEffect } from 'react/index';
import { DataSet } from 'choerodon-ui/pro';
import { withRouter } from 'react-router-dom';
import { inject } from 'mobx-react';
import AppDetailDataSet from './AppDetailDataSet';
import ServiceListDataSet from './ServiceListDataSet';
import AppVersionDataSet from './AppVersionDataSet';
import AppAllVersionDataSet from './AppAllVersionDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(inject('AppState')((props) => {
  const { AppState: { menuType: { type, orgId } }, children, match: { params: { id } }, code, history } = props;
  const appDetailDataSet = useMemo(() => new DataSet(AppDetailDataSet(id, orgId)), []);
  const appVersionDataSet = useMemo(() => new DataSet(AppVersionDataSet(id, orgId, false)), []);
  const appVersionStoreDataSet = useMemo(() => new DataSet(AppVersionDataSet(id, orgId, false)), []);
  const appAllVersionDataSet = useMemo(() => new DataSet(AppAllVersionDataSet(id, orgId)), []);
    
  const value = {
    ...props,
    appDetailDataSet,
    appVersionDataSet,
    appAllVersionDataSet,
    appVersionStoreDataSet,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
