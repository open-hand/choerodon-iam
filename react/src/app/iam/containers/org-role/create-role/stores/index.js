import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import map from 'lodash/map';
import MenuListDataSet from './MenuListDataSet';
import FormDataSet from './FormDataSet';
import LabelDataSet from './LabelDataSet';

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

  const roleLabelsDs = useMemo(() => new DataSet(LabelDataSet()), []);
  const menuDs = useMemo(() => new DataSet(MenuListDataSet({ level, organizationId })), [level, organizationId]);
  const formDs = useMemo(() => new DataSet(FormDataSet({ level, roleId, roleLabelsDs, organizationId, menuDs })), [level, roleId, organizationId]);

  async function loadData() {
    await axios.all([menuDs.query(), formDs.query()]);
    const menuList = map(formDs.current.get('menuIdList') || [], 'id');
    menuDs.forEach((record) => {
      if (menuList.includes(record.get('id'))) {
        record.init('isChecked', true);
      }
    });
  }

  useEffect(() => {
    if (level === 'project') {
      roleLabelsDs.query();
    }
    if (roleId) {
      loadData();
    } else {
      menuDs.query();
      formDs.create();
    }
  }, []);

  const value = {
    ...props,
    formDs,
    menuDs,
    prefixCls: 'base-org-role-create',
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
