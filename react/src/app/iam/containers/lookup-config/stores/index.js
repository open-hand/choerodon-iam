import React, { createContext, Children, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import LookupDataSet from './LookupDataSet';
import LookupValueDataSet from './LookupValueDataSet';

const Store = createContext();
export default Store;

const CODE_REGULAR_EXPRESSION = /^[a-zA-Z][a-zA-Z0-9-_.]*$/;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl } = props;
    const intlPrefix = 'organization.pwdpolicy';
    const lookupValueDataSet = useMemo(() => new DataSet(LookupValueDataSet({ CODE_REGULAR_EXPRESSION })));
    const lookupDataSet = useMemo(() => new DataSet(LookupDataSet({ intl, lookupValueDataSet, CODE_REGULAR_EXPRESSION })));
    
    const modalStyle = {
      width: 'calc(100% - 3.5rem)',
    };
    const permissions = [
     
    ];
    const value = {
      ...props,
      intl,
      intlPrefix,
      modalStyle,
      lookupDataSet,
      permissions,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
