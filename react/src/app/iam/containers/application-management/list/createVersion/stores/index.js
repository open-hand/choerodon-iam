import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { withRouter } from 'react-router-dom';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import AppServiceVersionDataSet from './AppServiceVersionDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children, applicationId } = props;
    const appServiceVersionDataSet = useMemo(() => new DataSet(AppServiceVersionDataSet({ id, intl, applicationId })), [id]);
    const intlPrefix = 'project.application-management.list';
    const value = {
      ...props,
      prefixCls: 'application-management',
      intlPrefix,
      projectId: id,
      applicationId,
      appServiceVersionDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
