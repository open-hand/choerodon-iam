import React, { createContext, useContext, useMemo, useEffect } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import map from 'lodash/map';
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
  const menuDs = useMemo(() => new DataSet(MenuListDataSet({ level, organizationId, roleId })), [level, organizationId]);
  const formDs = useMemo(() => new DataSet(FormDataSet({ level, roleId, roleLabelsDs, organizationId, menuDs })), [level, roleId, organizationId]);

  async function loadData() {
    function getNode(node, res, name = 'subMenus') {
      if (node.checkedFlag === 'Y') {
        node.isChecked = true;
      }
      res.push(node);
      if (node[name]) {
        node[name].forEach((n) => {
          getNode(n, res, name);
        });
      }
    }

    function getNodesByTree(tree, res, name = 'subMenus') {
      tree.forEach((node) => {
        getNode(node, res, name);
      });
    }

    const menuArray = [];
    await formDs.query();
    getNodesByTree(formDs.current.get('menuList'), menuArray, 'subMenus');
    menuDs.loadData(menuArray);
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
