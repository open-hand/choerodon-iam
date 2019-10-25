
import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Modal as OldModal, Tag } from 'choerodon-ui';
import { Table, Modal, message } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import CreateVersion from './createVersion';
import InRowTable from './InRowTable';

const { Column } = Table;
export default function ListView() {
  const context = useContext(Store);
  const { intlPrefix, permissions, intl, versionDataSet: dataSet, projectId, prefixCls, versionCreateDataSet, applicationId, tagMap } = context;
  function handleCreate() {
    versionCreateDataSet.loadData([]);
    versionCreateDataSet.create({ applicationId });
    Modal.open({
      title: '创建应用版本',
      drawer: true,
      children: <CreateVersion status="create" context={context} />,
      style: { width: '7.2rem' },
      className: `${prefixCls}-sider`,
      okText: '创建',
    });
  }
  function handleDelete(record) {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: '确认删除应用版本',
      content: `确认删除应用版本"${record.get('version')}"吗？`,
      onOk: async () => {
        try {
          const result = await axios.delete(`/base/v1/projects/${projectId}/applications/${applicationId}/versions/${record.get('id')}`);
          if (result.failed) {
            throw result.message;
          }
        } catch (err) {
          message.error(err);
        } finally {
          dataSet.query();
        }
      },
    });
  }
  function renderAction({ record }) {
    const actionDatas = [];
    if (record.get('status') === 'unpublished') {
      actionDatas.push({
        service: [permissions[3]],
        text: '删除',
        action: () => handleDelete(record),
      });
    }
    if (actionDatas.length === 0) {
      return;
    }
    return <Action data={actionDatas} />;
  }
  async function handleModify(record) {
    versionCreateDataSet.setQueryParameter('applicationId', applicationId);
    versionCreateDataSet.setQueryParameter('versionId', record.get('id'));
    await versionCreateDataSet.query();
    Modal.open({
      title: '修改应用版本',
      drawer: true,
      children: <CreateVersion status={record.get('status')} context={context} />,
      style: { width: '7.2rem' },
      className: `${prefixCls}-sider`,
      okText: '保存',
    });
  }
  function renderVersion({ text, record }) {
    const defaultChildren = <span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{text}</span>;
    if (record.get('status') === 'publishing') {
      return defaultChildren;
    }
    return (
      <Permission service={[permissions[4]]} defaultChildren={defaultChildren}>
        <span onClick={() => handleModify(record)} className="link">{text}</span>
      </Permission>
    );
  }
  function renderStatus({ value }) {
    const { text, color } = tagMap[value];
    return <Tag color={color}>{text}</Tag>;
  }
  return (
    <TabPage
      service={permissions}
    >
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Permission service={[permissions[2]]}>
          <Button icon="playlist_add" onClick={handleCreate}>创建应用版本</Button>
        </Permission>
      </Header>
      <Breadcrumb title="应用版本" />
      <Content className="application-management" style={{ paddingTop: 0 }}>
        <Table expandedRowRenderer={InRowTable} pristine dataSet={dataSet}>
          <Column renderer={renderVersion} name="version" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="description" />
          <Column renderer={renderStatus} name="status" />
        </Table>
      </Content>
    </TabPage>
  );
}
