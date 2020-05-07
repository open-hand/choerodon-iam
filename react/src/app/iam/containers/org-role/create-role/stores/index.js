import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import MenuListDataSet from './MenuListDataSet';
import FormDataSet from './FormDataSet';

const Store = createContext();

export function useCreateRoleStore() {
  return useContext(Store);
}

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const {
    AppState: { currentMenuType: { projectId, organizationId } },
    intl: { formatMessage },
    children,
    level,
    roleId,
  } = props;

  const prefix = useMemo(() => `role/${level}/default/`, [level]);
  const menuDs = useMemo(() => new DataSet(MenuListDataSet({ level })), [level]);
  const formDs = useMemo(() => new DataSet(FormDataSet({ level, prefix, roleId })), [level, roleId]);

  useEffect(() => {
    if (roleId) {
      formDs.query();
    } else {
      formDs.create();
    }
  }, []);

  const value = {
    ...props,
    formDs,
    menuDs,
    prefix,
    prefixCls: 'base-org-role-create',
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
