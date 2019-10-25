import React, { createContext, Children, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import MenuCodeDataSet from './MenuCodeDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl, level, orgId, projectFormListDataSet, organizationFormListDataSet } = props;
    const intlPrefix = 'organization.pwdpolicy';
    
    const menuCodeDataSet = useMemo(() => new DataSet(MenuCodeDataSet({ id: orgId })), [orgId]);
    const value = {
      ...props,
      intl,
      intlPrefix,
      projectFormListDataSet,
      organizationFormListDataSet,
      menuCodeDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
