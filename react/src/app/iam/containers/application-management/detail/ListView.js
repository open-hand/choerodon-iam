import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button } from 'choerodon-ui';
import { Table, Modal, message } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import Create from './create';

const { Column } = Table;
export default function ListView() {
  const context = useContext(Store);
  const { intlPrefix, permissions, intl, serviceDataSet: dataSet, prefixCls, applicationId, projectId, serviceTypeMap } = context;
  function handleCreate() {
    dataSet.create({ id: applicationId });
    Modal.open({
      title: '添加应用服务',
      drawer: true,
      children: <Create mode="add" context={context} />,
      style: { width: '7.2rem' },
      className: `${prefixCls}-sider`,
      okText: '保存',
    });
  }
  async function handleDelete(record) {
    try {
      const result = await axios.delete(`/base/v1/projects/${projectId}/applications/${applicationId}/services?service_ids=${record.get('id')}`);
      if (result.failed) {
        throw result.message;
      }
    } catch (err) {
      message.error(err);
    } finally {
      dataSet.query();
    }
  }
  function renderAction({ record }) {
    const actionDatas = [];
    if (!record.get('appServiceVersions') || record.get('appServiceVersions').length === 0) {
      actionDatas.push({
        service: [permissions[1]],
        text: intl.formatMessage({ id: 'delete' }),
        action: () => handleDelete(record),
      });
    }
    if (actionDatas.length === 0) { return; }
    return <Action data={actionDatas} />;
  }
  function renderName({ text, record }) {
    return text;
  }
  function renderType({ value }) {
    return serviceTypeMap[value];
  }
  return (
    <TabPage
      service={permissions}
    >
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Permission service={[permissions[0]]}>
          <Button icon="playlist_add" onClick={handleCreate}>添加应用服务</Button>
        </Permission>
      </Header>
      <Breadcrumb title="应用服务" />
      <Content style={{ paddingTop: 0 }}>
        <Table pristine dataSet={dataSet}>
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} renderer={renderName} name="name" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="code" />
          <Column renderer={renderType} style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="type" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="status" />
        </Table>
      </Content>
    </TabPage>
  );
}
