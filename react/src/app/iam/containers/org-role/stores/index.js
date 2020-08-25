import React, { createContext, useMemo, useState } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ListDataSet from './ListDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.role.list';
    const [level, setLevel] = useState('organization');
    const listDataSet = useMemo(
      () => new DataSet(ListDataSet({ level, organizationId })), [id, level],
    );
    const value = {
      ...props,
      listDataSet,
      prefixCls: 'base-org-role-list',
      intlPrefix,
      permissions: [
        'choerodon.code.organization.manager.role.ps.default',
      ],
      level,
      setLevel,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
