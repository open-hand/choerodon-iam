import React, { createContext, useMemo } from 'react/index';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { withRouter } from 'react-router-dom';
import AppCategoryDataSet from './AppCategoryDataSet';
import MarketAppsDataSet from './MarketAppsDataSet';
import DownloadRecordDataSet from './DownloadRecordDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, orgId } }, children } = props;
    const appCategoryDataSet = useMemo(() => new DataSet(AppCategoryDataSet()), []);
    const marketAppsDataSet = useMemo(() => new DataSet(MarketAppsDataSet(orgId)), []);
    const marketAppsStoreDataSet = useMemo(() => new DataSet(MarketAppsDataSet(orgId)), []);
    const downloadRecordDataSet = useMemo(() => new DataSet(DownloadRecordDataSet(orgId)), []);
    const value = {
      ...props,
      appCategoryDataSet,
      marketAppsDataSet,
      marketAppsStoreDataSet,
      downloadRecordDataSet,
      projectId: id,
      organizationId: orgId,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
