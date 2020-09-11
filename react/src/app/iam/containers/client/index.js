import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Table, Icon, Button, message, Modal, Row, Col,
} from 'choerodon-ui/pro';
import { Modal as OldModal } from 'choerodon-ui';
import {
  Content, Header, Page, axios, Action, Permission, TabPage, Breadcrumb,
} from '@choerodon/boot';

import { FormattedMessage } from 'react-intl';
import Store, { StoreProvider } from './store';

import EditRecord from './editRecord';
import CreateRecord from './createRecord';
import EditRole from './editRole';

const { Column } = Table;

const Client = observer(() => {
  const {
    clientDataSet, optionsDataSet, orgId, clientStore,
  } = useContext(Store);
  const [editModal, setEditModal] = useState(false);
  const [createModal, setCreateModal] = useState(false);
  const [editRoleModal, setEditRoleModal] = useState(false);
  function openEditRecordModal(record) {
    clientDataSet.current = record;
    setEditModal(true);
  }
  async function openCreateRecordModal() {
    const initData = await axios.get(`/iam/choerodon/v1/organizations/${orgId}/clients/createInfo`);
    initData.accessTokenValidity = 3600;
    initData.refreshTokenValidity = 3600;
    initData.autoApprove = 'default';
    initData.scope = 'default';
    initData.additionalInformation = '{}';

    await clientDataSet.create(initData);

    setCreateModal(true);
  }
  async function openRoleManageModal(record) {
    clientDataSet.current = record;
    const roleData = await clientStore.loadClientRoles(orgId, record.get('id'));
    const roleIds = (roleData || []).map(({ id: roleId }) => roleId);
    await record.set('roles', roleIds || []);
    setEditRoleModal(true);
  }
  function handleRowClick(record) {
    openEditRecordModal(record);
  }
  async function handleDelete(record) {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: '删除客户端',
      content: `确认删除客户端"${record.get('name')}"吗？`,
      maskClosable: false,
      okText: '删除',
      onOk: async () => {
        try {
          await axios.delete(`/iam/choerodon/v1/organizations/${orgId}/clients/${record.get('id')}`);
          await clientDataSet.query();
        } catch (err) {
          message.prompt(err);
        }
      },
    });
  }
  function handleRoleClick(record) {
    openRoleManageModal(record);
  }

  function renderAction({ record }) {
    const actionDatas = [{
      service: ['choerodon.code.organization.setting.client.ps.delete'],
      text: <FormattedMessage id="organization.client.delete.title" />,
      action: () => handleDelete(record),
    }, {
      service: ['choerodon.code.organization.setting.client.ps.role'],
      text: '角色分配',
      action: () => handleRoleClick(record),
    }];
    return <Action data={actionDatas} />;
  }
  function filterData(record) {
    return record.status !== 'add';
  }
  function renderName({ text, record }) {
    return (
      <Permission
        service={['choerodon.code.organization.setting.client.ps.update']}
        defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{text}</span>)}
      >
        <span role="none" className="link" onClick={() => handleRowClick(record)}>
          {text}
        </span>
      </Permission>
    );
  }
  return (
    <TabPage service={['choerodon.code.organization.setting.client.ps.default']}>
      <Header>
        <Permission service={['choerodon.code.organization.setting.client.ps.add']}>
          <Button color="blue" onClick={openCreateRecordModal}>
            <Icon type="playlist_add" />
            {' '}
            添加客户端
          </Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className="organization-pwdpolicy">
        <Table pristine filter={filterData} dataSet={clientDataSet} className="tab2">
          <Column renderer={renderName} width={250} name="name" align="left" />
          <Column width={50} renderer={renderAction} />
          <Column name="authorizedGrantTypes" width={500} />
        </Table>
        {editModal
        && (
        <EditRecord
          onOk={() => setEditModal(false)}
          onCancel={() => setEditModal(false)}
          dataSet={clientDataSet}
          record={clientDataSet.current}
          clientStore={clientStore}
        />
        )}
        {createModal
        && (
        <CreateRecord
          onOk={() => setCreateModal(false)}
          onCancel={() => setCreateModal(false)}
          dataSet={clientDataSet}
        />
        )}
        {editRoleModal
        && (
        <EditRole
          optionsDataSet={optionsDataSet}
          organizationId={orgId}
          onOk={() => setEditRoleModal(false)}
          onCancel={() => setEditRoleModal(false)}
          ds={clientDataSet}
          dataSet={optionsDataSet}
          record={clientDataSet.current}
        />
        )}
      </Content>
    </TabPage>
  );
});

export default (props) => (
  <StoreProvider {...props}>
    <Client />
  </StoreProvider>
);
