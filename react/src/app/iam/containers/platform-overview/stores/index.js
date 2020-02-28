import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { DataSet } from 'choerodon-ui/pro';
import OnlineCurrentDataset from './OnlineCurrentDataset';
import SystemNoticeDataset from './SystemNoticeDataset';
import SystemOptsDataset from './SystemOptsDataset';
import useStore from './useStore';
import OnlineHourDataset from './OnlineHourDataset';

const Store = createContext();

export function usePlatformOverviewStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { type, id, organizationId } }, intl,
  } = props;

  const platOverStores = useStore();
  const onlineNumDs = useMemo(() => new DataSet(OnlineCurrentDataset()), [id]); // 当前在线人数chart
  const onlineHourDs = useMemo(() => new DataSet(OnlineHourDataset()), [id]); // 当前每小时在线人数chart
  const noticeDs = useMemo(() => new DataSet(SystemNoticeDataset()), [id]); // 公告DS
  const optsDs = useMemo(() => new DataSet(SystemOptsDataset({ organizationId })), [id]); // 操作DS

  const value = {
    ...props,
    onlineNumDs,
    onlineHourDs,
    noticeDs,
    optsDs,
    platOverStores,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
