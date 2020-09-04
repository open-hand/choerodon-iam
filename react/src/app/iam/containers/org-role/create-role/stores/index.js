import React, {
  createContext, useContext, useMemo, useEffect,
} from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import forEach from 'lodash/forEach';
import MenuListDataSet from './MenuListDataSet';
import FormDataSet from './FormDataSet';
import LabelDataSet from './LabelDataSet';
import useStore from './useStore';

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

  const roleStore = useStore();
  const roleLabelsDs = useMemo(() => new DataSet(LabelDataSet()), []);
  const menuDs = useMemo(
    () => new DataSet(MenuListDataSet({ level, organizationId, roleId })), [level, organizationId],
  );
  const formDs = useMemo(() => new DataSet(FormDataSet({
    level, roleId, roleLabelsDs, organizationId, menuDs, formatMessage,
  })), [level, roleId, organizationId]);

  async function loadData() {
    await formDs.query();
    forEach(formDs.current.get('menuList'), (item) => {
      if (item.checkedFlag === 'Y') {
        // eslint-disable-next-line no-param-reassign
        item.isChecked = true;
      }
    });
    menuDs.loadData(formDs.current.get('menuList'));
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
    roleStore,
    prefixCls: 'base-org-role-create',
  };

  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
