import React, { createContext, useMemo, useReducer } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import ServiceTableDataSet from './ServiceTableDataSet';
import UpdateReleasedVersionDataSet from './UpdateReleasedVersionDataSet';

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
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, appId, versionId } = props;
    const serviceTableDataSet = useMemo(() => new DataSet(ServiceTableDataSet), []);
    const updateReleasedVersionDataSet = useMemo(() => new DataSet(UpdateReleasedVersionDataSet(id, organizationId, appId, versionId, serviceTableDataSet, mobxStore)), []);
    const value = {
      ...props,
      updateReleasedVersionDataSet,
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
