import React, { createContext, Children, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import OrganizationCategoryDataSet from './OrganizationCategoryDataSet';
import FormListDataSet from './FormListDataSet';
import OrganizationDataSet from './OrganizationDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl } = props;
    const intlPrefix = 'organization.pwdpolicy';
    const organizationCategoryDataSet = useMemo(() => new DataSet(OrganizationCategoryDataSet()));
    const projectFormListDataSet = useMemo(() => new DataSet(FormListDataSet({ level: 'project' })));
    const organizationFormListDataSet = useMemo(() => new DataSet(FormListDataSet({ level: 'organization' })));
    const organizationDataSet = useMemo(() => new DataSet(OrganizationDataSet({ intl })), []);
    
    const modalStyle = {
      width: 'calc(100% - 3.5rem)',
    };
    const value = {
      ...props,
      intl,
      intlPrefix,
      modalStyle,
      organizationCategoryDataSet,
      projectFormListDataSet,
      organizationFormListDataSet,
      organizationDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
