import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { FormattedMessage } from 'react-intl';
import { withRouter } from 'react-router-dom';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Modal as OldModal, Tooltip } from 'choerodon-ui';
import { Select, SelectBox, Table, TextField, Modal, message, Icon, Button } from 'choerodon-ui/pro';
import expandMoreColumn from '../../../components/expandMoreColumn';
import StatusTag from '../../../components/statusTag';
import Store from './stores';
import Sider from './sider';
import LdapModal from './ldapModal';

import './index.less';

const modalKey = Modal.key();
const syncModalKey = Modal.key();
const modalStyle = {
  width: 740,
};

const { Column } = Table;
export default withRouter(observer((props) => {
  const { intlPrefix,
    permissions, 
    intl: { formatMessage },
    AppState,
    orgUserListDataSet: dataSet, 
    organizationId, 
    orgUserCreateDataSet,
    orgUserRoleDataSet,
    orgRoleDataSet,
    orgAllRoleDataSet,
    passwordPolicyDataSet,
    userStore: { getCanCreate },
  } = useContext(Store);
  const modalProps = {
    create: {
      okText: '保存',
      title: '创建用户',
    },
    modify: {
      okText: '保存',
      title: '修改用户',
    },
    importUser: {
      okText: '确定',
      title: '导入用户',
    },
    roleAssignment: {
      okText: '确定',
      title: '添加组织用户',
    },
    addRole: {
      okText: '确定',
      title: '修改组织用户',
    },
    importRole: {
      okText: '返回',
      okCancel: false,
      title: '导入组织用户',
    },
  };
  async function handleDisable(record) {
    try {
      await axios.put(`/base/v1/organizations/${organizationId}/users/${record.get('id')}/disable`);
      const result = await dataSet.query();
      if (result.failed) {
        throw result.message;
      }
    } catch (err) {
      message.error(err);
    }
  }
  async function handleEnable(record) {
    try {
      const result = await axios.put(`/base/v1/organizations/${organizationId}/users/${record.get('id')}/enable`);
      if (result.failed) {
        throw result.message;
      }
      await dataSet.query();
    } catch (err) {
      return message.error(err);
    }
  }
  async function handleUnLock(record) {
    try {
      const result = await axios.put(`/base/v1/organizations/${organizationId}/users/${record.get('id')}/unlock`);
      if (result.failed) {
        throw result.message;
      }
      await dataSet.query();
    } catch (err) {
      message.error(err);
    }
  }
  async function resetPassword(userId) {
    try {
      const result = await axios.put(`/base/v1/organizations/${organizationId}/users/${userId}/reset`);
      if (!result.failed) {
        await dataSet.query();
      } else {
        message.error(result.message);
      }
    } catch (err) {
      message.error(err);
    }
  }

  function handleResetPassword(record) {
    Modal.open({
      key: modalKey,
      title: '确认重置当前用户密码',
      children: (
        <div>
          <p>{`"${record.get('realName')}"`}用户的当前密码将失效。如果您启用组织密码策略，将重置为组织默认密码，否则将重置为平台密码。</p>
        </div>
      ),
      onOk: () => resetPassword(record.get('id')),
    });
  }
  function handleSave() {
    dataSet.query();
  }
  function openModal(type) {
    Modal.open({
      ...modalProps[type],
      children: <Sider
        type={type}
        orgRoleDataSet={orgRoleDataSet}
        orgAllRoleDataSet={orgAllRoleDataSet}
        orgUserRoleDataSet={orgUserRoleDataSet}
        orgUserCreateDataSet={orgUserCreateDataSet}
        orgUserListDataSet={dataSet}
        onOk={handleSave}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      fullScreen: true,
      cancelText: '取消',
      destroyOnClose: true,
      className: 'base-org-user-sider',
    });
  }
  function handleModify(record) {
    dataSet.current = record;
    openModal('modify');
  }
  function handleUserRole(record) {
    const data = record.toData();
    data.roles = data.roles.map((v) => v.id);
    if (data.roles.length === 0) data.roles = [''];
    orgUserRoleDataSet.create(data);
    openModal('addRole');
  }
  function handleCreate() {
    let password = AppState.siteInfo.defaultPassword || 'abcd1234';
    if (passwordPolicyDataSet.current && passwordPolicyDataSet.current.get('enablePassword')) {
      password = passwordPolicyDataSet.current.get('originalPassword');
    }
    orgUserCreateDataSet.create({ roles: [''], password });
    openModal('create');
  }
  function handleRoleAssignment() {
    openModal('roleAssignment'); 
  }
  function handleImportUser() {
    openModal('importUser');
  }
  function handleImportRole() {
    openModal('importRole');
  }
  function handleDeleteUser(record) {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: '删除用户',
      content: `确认删除用户"${record.get('realName')}"在本组织下的全部角色吗?`,
      onOk: async () => {
        const result = await axios.put(`/base/v1/organizations/${organizationId}/users/${record.toData().id}/assign_roles`, []);
        if (!result.failed) {
          await orgUserRoleDataSet.reset();
          dataSet.query();
        } else {
          message.error(result.message);
          return false;
        }
      },
    });
  }

  function linkToLDAP() {
    const {
      history,
      location: {
        search,
      },
    } = props;
    history.push(`/base/organization-setting/ldap${search}`);
  }

  function handleSyncSetting() {
    Modal.open({
      key: syncModalKey,
      style: modalStyle,
      drawer: true,
      title: (
        <div className="org-user-sync-title">
          <span>LDAP同步设置</span>
          <Tooltip title="若同步过程中因为配置的问题无法执行，请点击“转至LDAP设置”按钮在详情界面进行配置">
            <Icon type="help" className="org-user-sync-title-icon" />
          </Tooltip>
        </div>
      ),
      children: <LdapModal />,
      okText: '手动同步',
      cancelText: '关闭',
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button
            color="primary"
            funcType="raised"
            onClick={linkToLDAP}
          >
            转至LDAP设置
          </Button>
          {cancelBtn}
        </div>
      ),
    });
  }

  function renderLocked({ value }) {
    return value ? '锁定' : '';
  }
  function rednerEnabled({ value }) {
    return <StatusTag name={value ? '启用' : '停用'} colorCode={value ? 'COMPLETED' : 'DEFAULT'} />;
  }
  function renderUserName({ value, record }) {
    if (record.get('organizationId').toString() !== organizationId) {
      return (
        <span>
          <Permission service={[permissions[6]]} defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{value}</span>)}>
            <span onClick={() => handleUserRole(record)} className="link">{value}</span>
          </Permission>
          <div className="org-user-external-user">
            <span className="org-user-external-user-text">
              外部人员
            </span>
          </div>
        </span>
      );
    }
    return (
      <Permission service={[permissions[1]]} defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{value}</span>)}>
        <span onClick={() => handleModify(record)} className="link">{value}</span>
      </Permission>
    );
  }
  function getQueryFields() {
    return {
      realName: <TextField clearButton labelLayout="float" />,
      roleName: <Select dropdownMenuStyle={{ width: 260 }} labelLayout="float" />,
      loginName: <TextField clearButton labelLayout="float" />,
      enabled: <SelectBox mode="button" />,
      locked: <SelectBox mode="button" />,
    };
  }
  function renderAction({ record }) {
    let actionDatas = [{
      service: [permissions[3]],
      text: <FormattedMessage id={`${intlPrefix}.action.reset`} />,
      action: () => handleResetPassword(record),
    }];
    // 外部人员的处理
    if (record.get('organizationId').toString() !== organizationId) {
      actionDatas = [{
        service: [permissions[8]],
        text: '删除',
        action: () => handleDeleteUser(record),
      }];
      return <Action data={actionDatas} />;
    }
    if (record.get('enabled')) {
      actionDatas.push({
        service: [permissions[5]],
        text: <FormattedMessage id={`${intlPrefix}.action.disable`} />,
        action: () => handleDisable(record),
      });
    } else {
      actionDatas.push({
        service: [permissions[4]],
        text: <FormattedMessage id={`${intlPrefix}.action.enable`} />,
        action: () => handleEnable(record),
      });
    }
    if (record.get('locked')) {
      actionDatas.push({
        service: [permissions[2]],
        text: <FormattedMessage id={`${intlPrefix}.action.unlock`} />,
        action: () => handleUnLock(record),
      });
    }
    return <Action data={actionDatas} />;
  }
  return (
    <TabPage
      service={permissions}
    >
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Tooltip
          title={getCanCreate ? '' : formatMessage({ id: `${intlPrefix}.button.create.disabled` })}
          placement="bottom"
        >
          <Button
            icon="playlist_add"
            disabled={!getCanCreate}
            onClick={handleCreate}
          >
            <FormattedMessage id={`${intlPrefix}.button.create-user`} />
          </Button>
        </Tooltip>
        <Tooltip
          title={getCanCreate ? '' : formatMessage({ id: `${intlPrefix}.button.create.disabled` })}
          placement="bottom"
        >
          <Button
            icon="archive"
            disabled={!getCanCreate}
            onClick={handleImportUser}
          >
            <FormattedMessage id={`${intlPrefix}.button.import-user`} />
          </Button>
        </Tooltip>
        <Button icon="person_add" onClick={handleRoleAssignment}>添加组织用户</Button>
        <Button icon="archive" onClick={handleImportRole}>导入组织用户</Button>
        <Button icon="compare_arrows" onClick={handleSyncSetting}>LDAP同步设置</Button>
      </Header>
      <Breadcrumb />
      <Content
        className="org-user"
      >
        <Table queryFields={getQueryFields()} queryFieldsLimit={3} labelLayout="float" pristine dataSet={dataSet}>
          <Column renderer={renderUserName} name="realName" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="loginName" />
          <Column renderer={rednerEnabled} name="enabled" align="left" />
          <Column minWidth={320} width={320} renderer={expandMoreColumn} className="org-user-roles" name="myRoles" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} renderer={renderLocked} name="locked" />
        </Table>
      </Content>
    </TabPage>
  );
}));
