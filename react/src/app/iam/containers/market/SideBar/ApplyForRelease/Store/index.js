import React, { createContext, useMemo, useReducer, useEffect } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import { useLocalStore } from 'mobx-react-lite';
import AppOptionDataSet from './Step1/AppOptionDataSet';
import PlatformAppTableDataSet from './Step1/PlatformAppTableDataSet';
import VersionOptionDataSet from './Step1/VersionOptionDataSet';
import VersionNameDataSet from './Step1/VersionNameDataSet';
import marketApp from './Step2/marketAppDataSet';
import CategoryTypeDataSet from './Step2/categoryTypeDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, HeaderStore: { orgData }, intl, children } = props;
    const appOptionDataSet = useMemo(() => new DataSet(AppOptionDataSet(id)), []);
    const versionOptionDataSet = useMemo(() => new DataSet(VersionOptionDataSet(id, appOptionDataSet)), []);
    const versionNameDataSet = useMemo(() => new DataSet(VersionNameDataSet(id, versionOptionDataSet)), []);
    const existPlatformAppTableDataSet = useMemo(() => new DataSet(PlatformAppTableDataSet('exist', id, versionOptionDataSet)), []);
    const newPlatformAppTableDataSet = useMemo(() => new DataSet(PlatformAppTableDataSet('new', id, versionOptionDataSet)), []);
    const categoryTypeDataSet = useMemo(() => new DataSet(CategoryTypeDataSet(id)), []);
    const marketAppDataSet = useMemo(() => new DataSet(marketApp(id, organizationId, categoryTypeDataSet)), []);
    const mobxStore = useLocalStore(
      source => ({
        createType: 'exist',
        latestVersionId: 0,
        setCreateType(inputType) {
          mobxStore.createType = inputType;
        },
        setLatestVersionId(inputLatestVersionId) {
          mobxStore.latestVersionId = inputLatestVersionId;
        },
      }),
    );

    const dataBuilder = async () => {
      await appOptionDataSet.query();
      const { contributor, notificationEmail } = await axios.get(`iam/choerodon/v1/projects/${id}/publish_applications/initial_info`, {
        params: {
          organization_id: organizationId,
        },
      });
      if (appOptionDataSet.length) {
        versionOptionDataSet.queryDataSet.current.set('refAppId', appOptionDataSet.get(0).toData());
        marketAppDataSet.current.set('name', appOptionDataSet.get(0).get('name'));
        marketAppDataSet.current.set('description', appOptionDataSet.get(0).get('description'));
        newPlatformAppTableDataSet.queryDataSet.current.set('applicationId', appOptionDataSet.get(0).get('id'));
        newPlatformAppTableDataSet.queryDataSet.current.set('applicationName', appOptionDataSet.get(0).get('name'));
        await versionOptionDataSet.query();
        await newPlatformAppTableDataSet.query();
      }
      marketAppDataSet.current.set('contributor', contributor);
      marketAppDataSet.current.set('notificationEmail', notificationEmail);

      if (versionOptionDataSet.length) {
        existPlatformAppTableDataSet.queryDataSet.current.set('versionObj', versionOptionDataSet.get(0).toData());
        await existPlatformAppTableDataSet.query();
      }
      await categoryTypeDataSet.query();
      marketAppDataSet.current.set('categoryOption', categoryTypeDataSet.get(0).toData());
    };

    useEffect(() => {
      dataBuilder();
    }, []);

    const value = {
      ...props,
      projectId: id,
      organizationId,
      mobxStore,
      existPlatformAppTableDataSet,
      newPlatformAppTableDataSet,
      appOptionDataSet,
      versionOptionDataSet,
      versionNameDataSet,
      marketAppDataSet,
      categoryTypeDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
