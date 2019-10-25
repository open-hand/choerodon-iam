import React, { createContext, useMemo, useContext } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { Choerodon } from '@choerodon/boot';
import TokenManagerDataSet from './TokenManagerDataSet';


const Store = createContext();
export function useStore() {
  return useContext(Store);
}
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'MenuStore')(
  (props) => {
    const { intl, children } = props;
    const intlPrefix = 'user.token-manager';

    const tokenManagerDataSet = useMemo(() => new DataSet(TokenManagerDataSet(Choerodon.getAccessToken().split(' ')[1], intl, intlPrefix)), []);
    const value = {
      ...props,
      // prefixCls: 'user-info',
      intlPrefix,
      permissions: [
        'base-service.access-token.list',
        'base-service.access-token.delete',
      ],
      tokenManagerDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
