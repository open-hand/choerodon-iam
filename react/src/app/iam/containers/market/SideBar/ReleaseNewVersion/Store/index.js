import React, { createContext, useMemo, useReducer, useEffect } from 'react/index';
import { withRouter } from 'react-router-dom';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import VersionDataSet from './VersionDataSet';
import ServiceTableDataSet from './serviceTableDataSet';
import AllServiceTableDataSet from './AllServiceTableDataSet';
import SelectVersionsDataSet from './SelectVersionsDataSet';

const Store = createContext();

export default Store;

const dataBuilder = async (versionDataSet, selectVersionsDataSet, serviceTableDataSet, allServiceTableDataSet) => {
  await selectVersionsDataSet.query();
  if (selectVersionsDataSet.length) {
    const canReleasedRecord = selectVersionsDataSet.find((record) => record.get('status') !== 'released');
    if (canReleasedRecord) {
      serviceTableDataSet.queryDataSet.current.set('version', canReleasedRecord.toData());
      await serviceTableDataSet.query();
    } else {
      versionDataSet.current.set('whetherToCreate', true);
    }
  }
  await allServiceTableDataSet.query();
  allServiceTableDataSet.forEach((item) => {
    item.set('appServiceVersions', item.get('allAppServiceVersions')[0]);
  });
};

export const StoreProvider = withRouter(injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children, queryUrl, refAppId, publishAppId, modal, status, versionId, name } = props;
    // const appId = 16;
    // const publishAppId = 1;
    // const url = `/base/v1/projects/${id}/publish_applications/${publishAppId}/new_version`
    const versionDataSet = useMemo(() => new DataSet(VersionDataSet(id, refAppId, queryUrl)), []);
    const selectVersionsDataSet = useMemo(() => new DataSet(SelectVersionsDataSet(id, refAppId)), []);
    const serviceTableDataSet = useMemo(() => new DataSet(ServiceTableDataSet(id, refAppId, selectVersionsDataSet)), []);
    const allServiceTableDataSet = useMemo(() => new DataSet(AllServiceTableDataSet(id, refAppId)), []);
    useEffect(() => {
      dataBuilder(versionDataSet, selectVersionsDataSet, serviceTableDataSet, allServiceTableDataSet);
    }, []);
    const value = {
      ...props,
      versionDataSet,
      serviceTableDataSet,
      selectVersionsDataSet,
      allServiceTableDataSet,
      modal,
      refAppId,
      publishAppId,
      versionId,
      status,
      projectId: id,
      organizationId,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
)));
