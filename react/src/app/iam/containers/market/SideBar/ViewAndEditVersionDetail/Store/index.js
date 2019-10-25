import React, { createContext, useMemo, useReducer } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import ServiceTableDataSet from './ServiceTableDataSet';
import ViewAndEditVersionDetailDataSet from './ViewAndEditVersionDetailDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
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
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, versionId } = props;
    const serviceTableDataSet = useMemo(() => new DataSet(ServiceTableDataSet), []);
    const viewAndEditVersionDetailDataSet = useMemo(() => new DataSet(ViewAndEditVersionDetailDataSet(id, versionId, serviceTableDataSet, mobxStore)), []);
    const value = {
      ...props,
      viewAndEditVersionDetailDataSet,
      serviceTableDataSet,
      mobxStore,
      organizationId,
      projectId: id,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
