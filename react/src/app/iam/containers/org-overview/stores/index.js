import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';

const Store = createContext();

export function useOrgOverview() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
    AppState: { currentMenuType: { type, id, organizationId } }, intl,
  } = props;

  const value = {
    ...props,
    permissions: [
      'devops-service.devops-organization.clusterOverview',
    ],
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
