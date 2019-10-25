import React, { createContext, Children, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ApprovalDataSet from './ApprovalDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl } = props;
    const intlPrefix = 'organization.pwdpolicy';
    const approvalDataSet = useMemo(() => new DataSet(ApprovalDataSet({ intl })));
    
    const modalStyle = {
      width: 'calc(100% - 3.5rem)',
    };
    const permissions = [
      'base-service.register-info.pagingQuery',
      'base-service.register-info.approval',
    ];
    const value = {
      ...props,
      intl,
      intlPrefix,
      modalStyle,
      approvalDataSet,
      permissions,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
