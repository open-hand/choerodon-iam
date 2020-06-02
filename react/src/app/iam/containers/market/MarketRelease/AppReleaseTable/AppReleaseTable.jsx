import React, { Component, useContext, useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Table, Modal, Button, Spin, Tooltip, message } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { axios, Content, Header, Page, Action, Breadcrumb, StatusTag } from '@choerodon/boot';

import Store from './Store';
import NoPermissionPage from '../../../../components/NoPermissionPage';
import ApplyForRelease from '../../SideBar/ApplyForRelease';
import ViewAndEditAppDetail from '../../SideBar/ViewAndEditAppDetail';
import ReleaseNewVersion from '../../SideBar/ReleaseNewVersion';
import AppReleaseSubTable from '../AppReleasedSubTable';
import ColorFunc from '../../../../utils/colorMap';
import { strLength } from '../../../../common/util';
import './AppReleaseTable.less';

const intlPrefix = 'project.app-release';
const cssPrefix = 'c7n-market-appRelease';
const { Column } = Table;
const modalKey = Modal.key();

const statusNameMap = new Map([
  ['published', '已发布'],
  ['unpublished', '未发布'],
  ['publishing', '发布中'],
]);

const apiReducer = (action, current, projectId, organizationId) => {
  switch (action) {
    case 'publishNewVersion':
      return `/iam/choerodon/v1/projects/${projectId}/publish_applications/${current.get('id')}/new_version`;
    default:
  }
};

const AppReleaseTable = observer((props) => {
  const { permissionDataSet, appReleasedTableDataSet, mobxStore, refresh, projectId, organizationId } = useContext(Store);

  const viewRecord = (record) => {
    Modal.open({
      title: '查看应用信息',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <ViewAndEditAppDetail
          appId={record.get('id')}
          mode="view"
          status={record.get('status')}
        />
      ),
      okText: '关闭',
      footer: (okBtn, cancelBtn) => okBtn,
    });
  };

  const editRecord = (record) => {
    Modal.open({
      title: '编辑应用信息',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <ViewAndEditAppDetail
          appId={record.get('id')}
          editReleased={record.get('editReleased')}
          status={record.get('status')}
          mode="edit"
        />
      ),
      okText: '保存',
      afterClose: () => {
        appReleasedTableDataSet.query();
      },
    });
  };

  const publishNewVersion = () => {
    Modal.open({
      title: '发布新版本',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <ReleaseNewVersion
          queryUrl={apiReducer('publishNewVersion', appReleasedTableDataSet.current, projectId)}
          refAppId={appReleasedTableDataSet.current.get('refAppId')}
          publishAppId={appReleasedTableDataSet.current.get('id')}
        />
      ),
      okText: '保存',
      afterClose: () => {
        appReleasedTableDataSet.query();
      },
    });
  };

  const statusMap = new Map([
    ['publishing', {
      name: '发布中',
      button: (record) => [{
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }],
    }],
    ['published', {
      name: '已发布',
      button: (record) => [{
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }, {
        service: [],
        text: '发布新版本',
        action: publishNewVersion,
      }],
    }],
    ['unpublished', {
      name: '未发布',
      button: (record) => [{
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }],
    }],
  ]);

  const ActionContainer = ({ record }) => {
    const { button } = statusMap.get(record.get('status'));
    return (
      <Action
        className="action-icon"
        data={button(record)}
        disabled={mobxStore.getDisableAllBtn || !record.get('appEditable')}
      />
    );
  };

  const IsFreeContainer = ({ value }) => <span>{value ? '是' : '否'}</span>;

  const PublishTypeContainer = ({ value }) => {
    switch (value) {
      case 'mkt_code_only':
        return (<span>源代码</span>);
      case 'mkt_deploy_only':
        return (<span>部署包</span>);
      case 'mkt_code_deploy':
        return (<span>源代码、部署包</span>);
      default:
    }
  };

  const StatusContainer = ({ value }) => <StatusTag name={statusMap.get(value).name} color={ColorFunc(value)} />;

  const handleRelease = (record) => {
    Modal.open({
      title: '申请发布应用',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <ApplyForRelease />
      ),
      afterClose: () => {
        appReleasedTableDataSet.query();
      },
    });
  };

  return (
    <Page
      service={['choerodon.route.market.market-publish']}
    >
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Button
          icon="publish2"
          funcType="flat"
          color="primary"
          disabled={mobxStore.getDisableAllBtn}
          onClick={handleRelease}
        >
          申请发布应用
        </Button>
      </Header>
      <Breadcrumb />
      <NoPermissionPage
        currentPermission={permissionDataSet.current}
        ShowDefaultPage={permissionDataSet.current && (!permissionDataSet.current.get('configurationValid') || !permissionDataSet.current.get('tokenValid') || (!permissionDataSet.current.get('publishingPermissionValid') && appReleasedTableDataSet.length === 0))}
        appReleasedTableDataSet={appReleasedTableDataSet}
      >
        <Content style={{ padding: '0 0.24rem' }}>
          <Table
            dataSet={appReleasedTableDataSet}
            className={`${cssPrefix}-table`}
            expandedRowRenderer={
              ({ dataSet, record }) => (
                <div style={{ marginLeft: '-0.12rem' }}><AppReleaseSubTable mobxStore={mobxStore} appId={record.get('id')} refresh={refresh} /></div>
              )
            }
          >
            <Column
              name="name"
              width={190}
              className={`${cssPrefix}-nameContainer`}
              onCell={({ record }) => ({
                onClick: () => viewRecord(record),
              })}
            />
            <Column renderer={ActionContainer} width={48} />
            <Column name="latestVersion" width={150} />
            <Column name="free" renderer={IsFreeContainer} align="left" />
            <Column name="publishType" renderer={PublishTypeContainer} />
            <Column name="status" renderer={StatusContainer} />
            <Column name="sourceAppName" tooltip="overflow" />
            <Column name="description" tooltip="overflow" />
          </Table>
        </Content>
      </NoPermissionPage>
    </Page>
  );
});

export default AppReleaseTable;
