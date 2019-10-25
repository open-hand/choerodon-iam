import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ApplicationDataSet from './ApplicationDataSet';
import VersionCreateDataSet from './VersionCreateDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children } = props;
    const intlPrefix = 'project.application-management.list';
    const applicationDataSet = useMemo(() => new DataSet(ApplicationDataSet({ id, intl, intlPrefix })), [id]);
    const versionCreateDataSet = useMemo(() => new DataSet(VersionCreateDataSet({ id, intl, intlPrefix })), [id]);
    const value = {
      ...props,
      prefixCls: 'application-management',
      intlPrefix,
      applicationDataSet,
      versionCreateDataSet,
      projectId: id,
      permissions: [
        'base-service.project-application.pagingAppByOptions', // 0 列表
        'base-service.project-application.createApplication', // 1 创建
        'base-service.project-app-service.pagingAppSvcByOptions', // 2 应用服务的列表
        'base-service.project-app-version.updateAppVersion', // 3 更新应用
        'base-service.project-application.deleteOrgCategory', // 4 删除应用
        'base-service.project-app-version.createAppVersion', // 5 创建应用版本
      ],
      serviceTypeMap: {
        normal: '普通应用',
        test: '测试应用',
      },
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
