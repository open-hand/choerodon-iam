import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';

import sagaDs from './SagaDataSet';
import taskDs from './TaskDataSet';
import sagaDetailDs from './SagaDetailDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const sagaDataSet = useMemo(() => new DataSet(sagaDs));
    const taskDataSet = useMemo(() => new DataSet(taskDs));
    const sagaDetailDataSet = useMemo(() => new DataSet(sagaDetailDs));
    const value = {
      ...props,
      prefixCls: 'c7n-saga-instance',
      sagaDataSet,
      taskDataSet,
      sagaDetailDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
