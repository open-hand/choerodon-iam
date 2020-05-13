import React, { createContext, useMemo, useState } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ListDataSet from './ListDataSet';
import LabelTipDataSet from './LabelTipDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.role.list';
    const [level, setLevel] = useState('organization');
    const listDataSet = useMemo(() => new DataSet(ListDataSet({ level })), [id, level]);
    const labelTipDataSet = useMemo(() => new DataSet(LabelTipDataSet({ level })), [id, level]);
    const value = {
      ...props,
      listDataSet,
      labelTipDataSet,
      prefixCls: 'base-org-role-list',
      intlPrefix,
      permissions: [],
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
