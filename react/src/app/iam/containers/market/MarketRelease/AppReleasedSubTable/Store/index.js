import React, { createContext, useMemo, useState, useEffect } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import AppReleasedSubTableDataSet from './AppReleasedSubTableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, appId, refresh } = props;
    const appReleasedSubTableDataSet = useMemo(() => new DataSet(AppReleasedSubTableDataSet(intl, id, appId)), [id, appId]);

    const value = {
      ...props,
      projectId: id,
      organizationId,
      appReleasedSubTableDataSet,
      refresh,
      // statusMap,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
