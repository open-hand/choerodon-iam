import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import OrgAdminListDataSet from './OrgAdminListDataSet';
import OrgAdminCreateDataSet from './OrgAdminCreateDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.admin.list';
    const orgAdminListDataSet = useMemo(() => new DataSet(OrgAdminListDataSet({ id, intl, intlPrefix })), [id]);
    const orgAdminCreateDataSet = useMemo(() => new DataSet(OrgAdminCreateDataSet({ id, organizationId, intl, intlPrefix })), [id]);
    const value = {
      ...props,
      orgAdminListDataSet,
      orgAdminCreateDataSet,
      prefixCls: 'base-org-admin-list',
      intlPrefix,
      permissions: [
        'choerodon.code.organization.manager.organization-admin.ps.default',
      ],
      organizationId,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
