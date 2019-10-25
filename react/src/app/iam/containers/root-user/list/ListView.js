import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import Store from './stores';
import Sider from './sider';
import './index.less';

const { Column } = Table;
export default function ListView() {
  const context = useContext(Store);
  const { intlPrefix, permissions, intl, adminListDataSet, adminCreateDataSet, prefixCls } = context;
  async function handleDelete({ record }) {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: '删除root用户',
      content: `确认删除root用户"${record.get('realName')}"吗?`,
      onOk: async () => {
        try {
          await axios.delete(`/base/v1/users/admin/${record.get('id')}`);
          await adminListDataSet.query();
        } catch (e) {
          return false;
        }
      },
    });
  }
  function renderAction(record) {
    const actionDatas = [{
      service: [permissions[2]],
      text: <FormattedMessage id={`${intlPrefix}.action.delete`} />,
      action: () => handleDelete(record),
    }];
    return <Action data={actionDatas} />;
  }
  function handleCreate() {
    adminCreateDataSet.create({ userName: [''] });
    Modal.open({
      title: intl.formatMessage({ id: 'global.root-user.sider.title' }),
      drawer: true,
      style: { width: '3.8rem', padding: '0' },
      children: <Sider
        adminCreateDataSet={adminCreateDataSet}
        adminListDataSet={adminListDataSet}
      />,
      className: 'base-root-user-sider-modal',
    });
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
      <Content style={{ paddingTop: 0 }}>
        <Table pristine dataSet={adminListDataSet}>
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="realName" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="loginName" />
        </Table>
      </Content>
    </TabPage>
  );
}
