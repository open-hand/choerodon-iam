import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { Table, Modal, message } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import Create from './create';
import CreateVersion from './createVersion';

const { Column } = Table;
export default function ListView() {
  const context = useContext(Store);
  const { intlPrefix, permissions, intl, applicationDataSet: dataSet, prefixCls, history, versionCreateDataSet, projectId } = context;
  function handleCreate() {
    dataSet.create();
    Modal.open({
      title: '创建应用',
      drawer: true,
      children: <Create mode="create" context={context} />,
      style: { width: '7.2rem' },
      className: `${prefixCls}-sider`,
      okText: '创建',
    });
  }
  function handleModify(record) {
    Modal.open({
      title: '修改应用信息',
      drawer: true,
      children: <Create mode="update" context={context} />,
      style: { width: '3.8rem' },
      className: `${prefixCls}-sider`,
      okText: '保存',
    });
  }
  async function handleDelete(record) {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: '确认删除应用',
      content: `确认删除应用"${record.get('name')}"吗？`,
      onOk: async () => {
        try {
          const result = await axios.delete(`/iam/choerodon/v1/projects/${projectId}/applications/${record.get('id')}`);
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
  function handleCreateVersion(record) {
    versionCreateDataSet.create({ applicationId: record.get('id') });
    Modal.open({
      title: '创建版本',
      drawer: true,
      children: <CreateVersion applicationId={record.get('id')} context={context} />,
      style: { width: '7.2rem' },
      className: `${prefixCls}-sider`,
      okText: '创建',
    });
  }
  function renderAction({ record }) {
    const actionDatas = [];
    actionDatas.push({
      service: [permissions[3]],
      text: '修改',
      action: handleModify,
    }, {
      service: [permissions[5]],
      text: '创建版本',
      action: () => handleCreateVersion(record),
    });
    if (record.get('amendable')) {
      actionDatas.push({
        service: [permissions[4]],
        text: '删除',
        action: () => handleDelete(record),
      });
    }
    return <Action data={actionDatas} />;
  }
  function handleDetail(record) {
    history.push(`/iam/choerodon/application-management/${record.get('id')}?${window.location.href.split('?').slice(1).join()}`);
  }
  function renderName({ text, record }) {
    return (
      <Permission service={[permissions[2]]} defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{text}</span>)}>
        <span onClick={() => handleDetail(record)} className="link">{text}</span>
      </Permission>
    );
  }
  return (
    <TabPage
      service={permissions}
    >
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Permission service={[permissions[1]]}>
          <Button icon="playlist_add" onClick={handleCreate}><FormattedMessage id={`${intlPrefix}.button.add`} /></Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className="application-management">
        <Table pristine dataSet={dataSet}>
          <Column renderer={renderName} name="name" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="description" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="creatorRealName" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="creationDate" />
        </Table>
      </Content>
    </TabPage>
  );
}
