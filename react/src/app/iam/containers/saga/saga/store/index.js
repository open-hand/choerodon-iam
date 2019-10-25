import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';

import TableDataSet from './TableDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { children } = props;
    const dataSet = useMemo(() => new DataSet(TableDataSet));
    const value = {
      ...props,
      prefixCls: 'c7n-saga',
      dataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
