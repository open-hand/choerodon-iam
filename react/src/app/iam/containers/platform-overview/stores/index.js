import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { DataSet } from 'choerodon-ui/pro';
import OnlineCurrentDataset from './OnlineCurrentDataset';

const Store = createContext();

export function usePlatformOverviewStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { type, id, organizationId } }, intl,
  } = props;

  const onlineNumDs = useMemo(() => new DataSet(OnlineCurrentDataset()), [id]); // 当前在线人数chart


  const value = {
    ...props,
    onlineNumDs,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
