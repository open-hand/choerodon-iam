import React, { createContext, useMemo, useReducer } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import optionDataSet from './optionDataSet';
import ViewAndEditAppDetailDataSet from './ViewAndEditAppDetailDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, appId, status, editReleased } = props;
    const mobxStore = useLocalStore(
      source => ({
        overview: '',
        setOverview(inputOverview) {
          mobxStore.overview = inputOverview;
        },
      }),
    );
    const categoryTypeDataSet = useMemo(() => new DataSet(optionDataSet(id)), []);
    const viewAndEditAppDetailDataSet = useMemo(() => new DataSet(ViewAndEditAppDetailDataSet(id, appId, mobxStore, status, editReleased, categoryTypeDataSet)), []);
    const value = {
      ...props,
      viewAndEditAppDetailDataSet,
      mobxStore,
      projectId: id,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
