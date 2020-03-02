import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { DataSet } from 'choerodon-ui/pro';

import failedStatisticsTableDataSet from './failedStatisticsTableDataSet';
import useStore from './useStore';

const Store = createContext();

export function useFailedStatisticsStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: {
      menuType: { orgId },
    },
  } = props;

  const FailedStatisticsTableDataSet = useMemo(() => new DataSet(failedStatisticsTableDataSet({ orgId })), []);

  const ThingPerformStore = useStore();

  const value = {
    ...props,
    FailedStatisticsTableDataSet,
    ThingPerformStore,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
