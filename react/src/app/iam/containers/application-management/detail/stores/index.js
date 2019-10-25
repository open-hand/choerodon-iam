import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import ServiceDataSet from './ServiceDataSet';
import VersionDataSet from './VersionDataSet';
import VersionCreateDataSet from '../../list/stores/VersionCreateDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id } }, intl, children, match: { params: { applicationId } } } = props;
    const intlPrefix = 'project.application-management.list';
    const serviceDataSet = useMemo(() => new DataSet(ServiceDataSet({ id, intl, intlPrefix, applicationId })), [id]);
    const versionDataSet = useMemo(() => new DataSet(VersionDataSet({ id, intl, intlPrefix, applicationId })), [id]);
    const versionCreateDataSet = useMemo(() => new DataSet(VersionCreateDataSet({ id, intl, intlPrefix, applicationId })), [id]);
    const value = {
      ...props,
      prefixCls: 'application-management',
      intlPrefix,
      serviceDataSet,
      versionDataSet,
      versionCreateDataSet,
      applicationId,
      projectId: id,
      permissions: [
        'base-service.project-app-service.addAppSvcRef', // 0 添加应用服务
        'base-service.project-app-service.deleteAppSvcRef', // 1 删除应用服务
        'base-service.project-app-version.createAppVersion', // 2 创建应用版本
        'base-service.project-app-version.deleteAppVersion', // 3 删除应用版本
        'base-service.project-app-version.updateAppVersion', // 4 修改应用版本
      ],
      serviceTypeMap: {
        normal: '普通应用',
        test: '测试应用',
      },
      tagMap: {
        publishing: {
          color: 'rgba(77,144,254,1)',
          text: '发布中',
        },
        unpublished: {
          color: 'rgba(0,0,0,0.2)',
          text: '未发布',
        },
        published: {
          color: 'rgba(0,191,165,1)',
          text: '已发布',
        },
      },
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
