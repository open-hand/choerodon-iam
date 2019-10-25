import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ApplicationServiceDataSet from './ApplicationServiceDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const intlPrefix = 'project.application-management.list';
    const sharedServiceDataSet = useMemo(() => new DataSet(ApplicationServiceDataSet({ id, intl, intlPrefix, type: 'shared' })), [id]);
    const projectServiceDataSet = useMemo(() => new DataSet(ApplicationServiceDataSet({ id, intl, intlPrefix, type: 'project' })), [id]);
    const value = {
      ...props,
      prefixCls: 'application-management',
      intlPrefix,
      sharedServiceDataSet,
      projectServiceDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
