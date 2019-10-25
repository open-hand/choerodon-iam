import React, { createContext, useMemo, useState, useEffect } from 'react';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { message } from 'choerodon-ui';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import AppReleasedTableDataSet from './AppReleasedTableDataSet';
import PermissionDataSet from './PermissionDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const permissionDataSet = useMemo(() => new DataSet(PermissionDataSet(intl, id)), []);
    const appReleasedTableDataSet = useMemo(() => new DataSet(AppReleasedTableDataSet(intl, id)), []);

    const mobxStore = useLocalStore(() => ({
      disableAllBtn: false,
      get getDisableAllBtn() {
        return mobxStore.disableAllBtn;
      },
      setDisable(status) {
        mobxStore.disableAllBtn = status;
      },
    }));

    const permissionInit = async () => {
      await permissionDataSet.query();
      const { current } = permissionDataSet;
      // configurationValid: true
      // publishingPermissionValid: true
      // tokenValid: true
      if (!current.get('configurationValid') || !current.get('tokenValid')) {
        mobxStore.setDisable(true);
        return Promise.reject(Error('未配置远程连接'));
      }
      if (!current.get('publishingPermissionValid')) {
        mobxStore.setDisable(true);
        return Promise.resolve(true);
      }
      if (!current.get('updateSuccessFlag')) {
        message.error('应用更新失败');
        mobxStore.setDisable(true);
        return Promise.resolve(true);
      }
      mobxStore.setDisable(false);
      return Promise.resolve(true);
    };
    const refresh = () => permissionInit().then((r) => {
      appReleasedTableDataSet.query();
    });
    useEffect(() => {
      refresh();
    }, []);
    const value = {
      ...props,
      projectId: id,
      permissionDataSet,
      appReleasedTableDataSet,
      mobxStore,
      refresh,
      // statusMap,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
