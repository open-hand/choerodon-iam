import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { DataSet } from 'choerodon-ui/pro';

import AppOverviewDataset from './AppOverviewDataset';
import OptsOverviewDataset from './OptsOverviewDataset';
import useStore from './useStore';

const Store = createContext();

export function useOrgOverviewRightSide() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { type, id, organizationId } }, intl,
  } = props;

  const overStores = useStore();
  const appServiceDs = useMemo(() => new DataSet(AppOverviewDataset({ organizationId })), [id]); // pieChart应用服务概览DS
  const optsDs = useMemo(() => new DataSet(OptsOverviewDataset({ organizationId })), [id]); // 最近操作概览DS

  const value = {
    ...props,
    appServiceDs,
    optsDs,
    overStores,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
