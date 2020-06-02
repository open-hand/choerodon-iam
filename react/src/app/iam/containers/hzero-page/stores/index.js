import React, { createContext, useContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { withRouter } from 'react-router-dom';

const Store = createContext();

export function useHzeroPageStore() {
  return useContext(Store);
}

export const StoreProvider = withRouter(injectIntl(inject('AppState')((props) => {
  const {
    children,
    location: { pathname },
  } = props;

  const pageType = useMemo(() => (pathname.match(/\/hzero\/(\S*)/))[1] || '', [pathname]);

  const value = {
    ...props,
    prefixCls: 'c7n-hzero-page',
    intlPrefix: 'c7n.hzero.page',
    pageType,
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
})));
