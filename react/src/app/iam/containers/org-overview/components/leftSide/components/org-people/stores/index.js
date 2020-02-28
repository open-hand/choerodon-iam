import React, { createContext, useContext } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';

import useStore from './useStore';

const Store = createContext();

export function useOrgPeopleStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')(observer((props) => {
  const {
    children,
  } = props;

  const OrgPeopleStore = useStore();

  const value = {
    ...props,
    OrgPeopleStore,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
