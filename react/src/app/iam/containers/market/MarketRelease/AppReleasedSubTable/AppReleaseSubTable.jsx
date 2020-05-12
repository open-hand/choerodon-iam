import React, { Component, useContext, useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Table, Modal, Button, Spin, Tooltip, message } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import classnames from 'classnames';
import { Icon } from 'choerodon-ui';
import { FormattedMessage } from 'react-intl';
import { axios, Content, Header, Page, Action, Breadcrumb, TabPage, StatusTag } from '@choerodon/boot';

import Store from './Store';
import { strLength } from '../../../../common/util';
import ColorFunc from '../../../../utils/colorMap';
import ViewAndEditVersionDetail from '../../SideBar/ViewAndEditVersionDetail';
import ConfirmApp from '../../SideBar/ConfirmApp';
import UpdateReleasedVersion from '../../SideBar/UpdateReleasedVersion';
import ExpandMoreColumn from '../../component/expandMoreColumn';
import './AppReleaseSubTable.less';
// import StatusTag from '../../../../components/statusTag';

const intlPrefix = 'project.app-release';
const cssPrefix = 'c7n-market-AppReleasedSubTable';
const { Column } = Table;
const modalKey = Modal.key();

const publishErrorCodeMap = new Map([
  ['error.git.clone', '仓库 clone 失败'],
  ['error.chart.empty', 'chart 为空'],
  ['error.upload.file', '上传文件失败'],
  ['error.download.chart', '下载 chart 失败'],
  ['error.param.replace', '替换镜像地址失败'],
  ['error.zip.repository', 'zip 打包失败'],
  ['error.exec.push.image', 'push 镜像失败'],
]);

const statusNameMap = new Map([
  ['published', '已发布'],
  ['unpublished', '未发布'],
  ['under_approval', '审批中'],
  ['unconfirmed', '待确认'],
  ['rejected', '被驳回'],
  ['withdrawn', '已撤销'],
]);

const AppReleaseSubTable = observer((props) => {
  const { appReleasedSubTableDataSet, projectId, organizationId, appId, refresh, mobxStore } = useContext(Store);
  // todo: 在顶层写一个 cache Arr，不刷新在 cache 中的记录
  // const afterClose = async () => {
  //   const currentExpand = appReleasedTableDataSet.filter((item) => {
  //     return item.get('expand')
  //   });
  //   await appReleasedTableDataSet.query();
  //   appReleasedTableDataSet.forEach((item) => currentExpand.find((expandItem) => expandItem.get('id') === item.get('id')) && item.set('expand', true));
  // };

  // 所有按钮、操作需要的 api

  const apiReducer = (action, record) => {
    switch (action) {
      case 'delete':
        return axios.delete(`iam/choerodon/v1/projects/${projectId}/publish_applications/${record.get('mktAppId')}/versions/${record.get('id')}`);
      case 'revert':
        return axios.put(`iam/choerodon/v1/projects/${projectId}/publish_applications/${record.get('mktAppId')}/versions/${record.get('id')}/revocation`);
      case 'submit':
        return axios.get(`iam/choerodon/v1/projects/${projectId}/publish_applications/${record.get('mktAppId')}/versions/${record.get('id')}/apply`, { params: { organization_id: organizationId } });
      case 'retry':
        return axios.put(`iam/choerodon/v1/projects/${projectId}/publish_applications/${record.get('mktAppId')}/versions/${record.get('id')}/republish`, null, { params: { organization_id: organizationId } });
      case 'refix':
        return axios.get(`iam/choerodon/v1/projects/${projectId}/publish_applications/${record.get('mktAppId')}/versions/${record.get('id')}/refix`);
      default:
    }
  };

  const deleteRecord = (record, type = 'delete') => Modal.confirm({
    key: Modal.key(),
    children: <span>{`确认要${type === 'delete' ? '删除' : '撤销'}该条记录吗？`}</span>,
  }).then((button) => {
    if (button === 'ok') {
      return apiReducer(type, record).then((res) => {
        if (res.failed) {
          message.error(`${type === 'delete' ? '删除' : '撤销'}失败`);
        } else {
          message.success(`${type === 'delete' ? '删除' : '撤销'}成功`);
        }
        if (type === 'delete') {
          refresh();
        } else {
          appReleasedSubTableDataSet.query();
        }
      });
    }
  });

  const submitRecord = (record) => apiReducer('submit', record).then((res) => {
    if (!res) {
      message.error('申请失败');
      return Promise.reject();
    }
    if (res.failed) {
      message.error(res.message);
      return Promise.reject();
    }
    appReleasedSubTableDataSet.query();
    message.success('申请成功');
    return Promise.resolve();
  });

  const retryRecord = (record) => apiReducer('retry', record).then((res) => {
    if (!res) {
      message.error('重试失败');
      return Promise.reject();
    }
    if (res.failed) {
      message.error(res.message);
      return Promise.reject();
    }
    appReleasedSubTableDataSet.query();
    message.success('重试成功');
    return Promise.resolve();
  });

  const refixRecord = (record) => apiReducer('refix', record).then((res) => {
    if (!res) {
      message.error('重试失败');
      return Promise.reject();
    }
    if (res.failed) {
      message.error(res.message);
      return Promise.reject();
    }
    appReleasedSubTableDataSet.query();
    message.success('重试成功');
    return Promise.resolve();
  });

  // 点击按钮后触发的动作

  const viewVersion = (record) => {
    Modal.open({
      title: '查看版本信息',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: <ViewAndEditVersionDetail versionId={record.get('id')} mode="view" status={record.get('status')} />,
      okText: '关闭',
      footer: (okBtn, cancelBtn) => okBtn,
    });
  };

  const editRecord = (record) => {
    Modal.open({
      title: `编辑${statusNameMap.get(record.get('status'))}应用版本`,
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: <ViewAndEditVersionDetail versionId={record.get('id')} mode="edit" status={record.get('status')} />,
      okText: '保存',
      afterClose: () => {
        appReleasedSubTableDataSet.query();
      },
    });
  };

  const maintainRecord = (record) => {
    Modal.open({
      title: '确认信息',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <ConfirmApp
          appId={record.get('mktAppId')}
          versionId={record.get('id')}
        />
      ),
      okText: '保存',
      afterClose: () => {
        appReleasedSubTableDataSet.query();
      },
    });
  };

  const updateVersion = (record) => {
    Modal.open({
      title: '更新已发布应用版本',
      key: modalKey,
      drawer: true,
      style: {
        width: '51.39%',
      },
      children: (
        <UpdateReleasedVersion
          appId={record.get('mktAppId')}
          versionId={record.get('id')}
        />
      ),
      okText: '更新',
      afterClose: () => {
        appReleasedSubTableDataSet.query();
      },
    });
  };

  // 状态所对应的按钮

  const statusMap = new Map([
    ['published', {
      name: '已发布',
      button: (record) => {
        const updateRecordArr = [{
          service: [],
          text: '更新版本',
          action: () => updateVersion(record),
        }];
        const retryRecordArr = record.get('publishErrorCode') ? [{
          service: [],
          text: '重试',
          action: () => refixRecord(record),
        }] : [];
        if (record.get('publishErrorCode')) {
          return [...updateRecordArr, ...retryRecordArr];
        } else {
          return updateRecordArr;
        }
      },
    }],
    ['unpublished', {
      name: '未发布',
      button: (record) => [{
        service: [],
        text: '删除',
        action: () => deleteRecord(record),
      }, {
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }, {
        service: [],
        text: '提交申请',
        action: () => submitRecord(record),
      }],
    }],
    ['under_approval', {
      name: '审批中',
      button: (record) => [{
        service: [],
        text: '撤销',
        action: () => deleteRecord(record, 'revert'),
      }],
    }],
    ['unconfirmed', {
      name: '待确认',
      button: (record) => {
        const editRecordArr = [{
          service: [],
          text: '确认信息',
          action: () => maintainRecord(record),
        }];
        const retryRecordArr = record.get('publishErrorCode') ? [{
          service: [],
          text: '重试',
          action: () => retryRecord(record),
        }] : [];
        if (record.get('publishErrorCode')) {
          return [...editRecordArr, ...retryRecordArr];
        } else {
          return editRecordArr;
        }
      },
    }],
    ['rejected', {
      name: '被驳回',
      button: (record) => [{
        service: [],
        text: '删除',
        action: () => deleteRecord(record),
      }, {
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }],
    }],
    ['withdrawn', {
      name: '已撤销',
      button: (record) => [{
        service: [],
        text: '删除',
        action: () => deleteRecord(record),
      }, {
        service: [],
        text: '编辑',
        action: () => editRecord(record),
      }],
    }],
  ]);

  // 渲染 Table 时需要用到的 Container

  const PublishStatus = (record) => {
    if (record.get('publishing')) {
      return (
        <span className={`${cssPrefix}-publishing`}>
          <Icon type="timelapse" />
        </span>
      );
    }
    if (record.get('publishErrorCode')) {
      return <Tooltip title="发布失败"><Icon type="error" style={{ color: 'red' }} /></Tooltip>;
    }
    return null;
  };

  const AppNameContainer = ({ value, record }) => (
    <div className={`${cssPrefix}-nameContainer-innerContainer`}>
      <Tooltip title={value}>
        <span className={`${cssPrefix}-nameContainer-innerContainer-name`}>
          {value}
        </span>
      </Tooltip>
      {PublishStatus(record)}
    </div>
  );

  const ActionContainer = ({ record }) => {
    const { button } = statusMap.get(record.get('status'));
    return (
      <Action
        className="action-icon"
        data={button(record)}
        disabled={mobxStore.getDisableAllBtn || record.get('publishing') === true}
      />
    );
  };

  const StatusContainer = ({ value, record }) => {
    if (value === 'rejected') {
      const strL = strLength(value);
      let width;
      if (strL > 340) {
        width = '3.4rem';
      } else {
        width = `${strL / 100}rem`;
      }
      return (
        <Tooltip title={<p style={{ width, whiteSpace: 'normal', marginBottom: 0 }}>{record.get('approveMessage')}</p>}>
          <StatusTag name={statusMap.get(value).name} color={ColorFunc(value)} />
        </Tooltip>
      );
    }
    return (
      <StatusTag name={statusMap.get(value).name} color={ColorFunc(value)} />
    );
  };

  return (
    <Table dataSet={appReleasedSubTableDataSet} className={`${cssPrefix}-table`} queryBar="none">
      <Column
        name="version"
        width={190}
        className={`${cssPrefix}-nameContainer`}
        onCell={({ record }) => ({
          onClick: () => viewVersion(record),
        })}
        renderer={AppNameContainer}
      />
      <Column renderer={ActionContainer} width={48} />
      <Column name="status" renderer={StatusContainer} width={150} />
      <Column
        name="containServices"
        header="应用服务"
        width={600}
        renderer={(renderProps) => (
          <ExpandMoreColumn
            {...renderProps}
            maxLength={600}
            nameField="name"
          />
        )}
        className={`${cssPrefix}-serviceContainer`}
      />
    </Table>
  );
});

export default AppReleaseSubTable;
