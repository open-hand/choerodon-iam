import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { DataSet } from 'choerodon-ui/pro';
import proDeploySelectDataSet from './ProDeploySelectDataSet';

import useStore from './useStore';

const Store = createContext();

export function useProDeployStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: {
      menuType: { orgId },
    },
  } = props;


  const ProDeployStore = useStore();

  const ProDeploySelectDataSet = useMemo(() => new DataSet(proDeploySelectDataSet({ orgId, ProDeployStore })), [orgId]);

  const value = {
    ...props,
    ProDeployStore,
    ProDeploySelectDataSet,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
