import React, { createContext, useMemo, useReducer } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import ConfirmAppDataSet from './ConfirmAppDataSet';
import ServiceTableDataSet from './ServiceTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, appId, versionId } = props;
    const mobxStore = useLocalStore(
      source => ({
        overview: '',
        document: '',
        setOverview(inputOverview) {
          mobxStore.overview = inputOverview;
        },
        setDocument(inputDocument) {
          mobxStore.document = inputDocument;
        },
      }),
    );
    const serviceTableDataSet = useMemo(() => new DataSet(ServiceTableDataSet), []);
    const confirmAppDataSet = useMemo(() => new DataSet(ConfirmAppDataSet(id, appId, versionId, serviceTableDataSet, mobxStore)), []);
    const value = {
      ...props,
      confirmAppDataSet,
      serviceTableDataSet,
      mobxStore,
      projectId: id,
      organizationId,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
