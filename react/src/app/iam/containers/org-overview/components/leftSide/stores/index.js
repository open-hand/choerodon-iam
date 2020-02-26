import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import ClusterOverviewDataset from './ClusterOverviewDataset';
import ProjectOverviewDataset from './ProjectOverviewDataset';

const Store = createContext();

export function useOrgOverviewLeftSide() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { type, id, organizationId } }, intl,
  } = props;

  const clusterDs = useMemo(() => new DataSet(ClusterOverviewDataset({ organizationId })), [id]); // 集群概览DS
  const projectDs = useMemo(() => new DataSet(ProjectOverviewDataset({ organizationId })), [id]); // 项目概览

  const value = {
    ...props,
    clusterDs,
    projectDs,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
