import React, { useContext, useState, Fragment } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { Select, SelectBox, Table, TextField, Modal, message, Radio } from 'choerodon-ui/pro';
import expandMoreColumn from '../../../components/expandMoreColumn';
import DeleteRoleModal from '../DeleteRoleModal';
import StatusTag from '../../../components/statusTag';
import Store from './stores';
import Sider from './sider';
import './index.less';

const modalKey = Modal.key();

let InviteModal = false;
try {
  const { default: requireData } = require('@choerodon/base-pro/lib/routes/invite-user');
  InviteModal = requireData;
} catch (error) {
  InviteModal = false;
}

const { Column } = Table;
export default function ListView(props) {
  const { intlPrefix,
    orgUserListDataSet: dataSet,
    projectId,
    orgUserCreateDataSet,
    orgUserRoleDataSet,
    organizationId,
    orgRoleDataSet,
    allRoleDataSet,
    AppState,
  } = useContext(Store);

  const [deleteRoleRecord, setDeleteRoleRecord] = useState(undefined);

  const modalProps = {
    create: {
      okText: '保存',
      title: '添加团队成员',
    },
    addRole: {
      okText: '保存',
      title: '修改团队成员',
    },
    invite: {
      okText: '发送邀请',
      title: '邀请成员',
    },
    importRole: {
      okText: '返回',
      okCancel: false,
      title: '导入团队成员',
    },
  };
  function handleSave() {
    dataSet.query();
  }
  function openModal(type) {
    Modal.open({
      ...modalProps[type],
      children: <Sider
        type={type}
        allRoleDataSet={allRoleDataSet}
        orgRoleDataSet={orgRoleDataSet}
        orgUserRoleDataSet={orgUserRoleDataSet}
        orgUserCreateDataSet={orgUserCreateDataSet}
        orgUserListDataSet={dataSet}
        onOk={handleSave}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      fullScreen: true,
      destroyOnClose: true,
      className: 'base-project-user-sider',
    });
  }
  function handleUserRole(record) {
    const data = record.toData();
    data.roles = data.roles.map((v) => v.id);
    if (data.roles.length === 0) data.roles = [''];
    orgUserRoleDataSet.create(data);
    openModal('addRole');
  }
  function handleCreate() {
    openModal('create');
  }
  function handleImportRole() {
    openModal('importRole');
  }

  const handleCancel = () => {
    setDeleteRoleRecord(undefined);
    handleSave();
  };

  function handleDeleteUser(record) {
    const roleIds = (record.get('roles') || []).map(({ id }) => id);
    const postData = {
      memberType: 'user',
      view: 'userView',
      sourceId: Number(projectId),
      data: { [record.get('id')]: roleIds },
    };
    if (InviteModal && AppState.menuType.category === 'PROGRAM') {
      setDeleteRoleRecord(record);
    } else {
      OldModal.confirm({
        className: 'c7n-iam-confirm-modal',
        title: '删除用户',
        content: `确认删除用户"${record.get('realName')}"在本项目下的全部角色吗?`,
        onOk: async () => {
          const result = await axios.post(`/iam/choerodon/v1/projects/${projectId}/users/${record.get('id')}/role_members/delete`, JSON.stringify(postData));
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
  }
  function rednerEnabled({ value }) {
    return <StatusTag name={value ? '启用' : '停用'} colorCode={value ? 'COMPLETED' : 'DEFAULT'} />;
  }
  // 外部人员
  function renderUserName({ value, record }) {
    const label = record.get('organizationId').toString() !== organizationId ? (
      <div className="project-user-external-user">
        <span className="project-user-external-user-text">
          外部人员
        </span>
      </div>
    ) : null;
    // const programLabel = InviteModal && record.get('programOwner') ? (
    const programLabel = record.get('programOwner') ? (
      <div className="project-user-external-user">
        <span className="project-user-external-user-text">
          项目群人员
        </span>
      </div>
    ) : null;
    return (
      <Fragment>
        <Permission
          service={['choerodon.code.project.cooperation.team-member.ps.update']}
          defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{value}</span>)}
        >
          <span onClick={() => handleUserRole(record)} className="link">{value}</span>
        </Permission>
        {label}
        {programLabel}
      </Fragment>
    );
  }

  function renderAction({ record }) {
    const actionDatas = [{
      service: ['choerodon.code.project.cooperation.team-member.ps.delete'],
      text: '删除',
      action: () => handleDeleteUser(record),
    }];
    return InviteModal && record.get('programOwner') ? '' : <Action data={actionDatas} />;
  }

  function getInitialButton() {
    if (InviteModal) {
      return (
        <InviteModal
          allRoleDataSet={allRoleDataSet}
          orgRoleDataSet={orgRoleDataSet}
          orgUserRoleDataSet={orgUserRoleDataSet}
          orgUserCreateDataSet={orgUserCreateDataSet}
          orgUserListDataSet={dataSet}
          onOk={handleSave}
        />
      );
    }
  }

  return (
    <TabPage service={['choerodon.code.project.cooperation.team-member.ps.default']}>
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Permission service={['choerodon.code.project.cooperation.team-member.ps.add']}>
          <Button
            icon="person_add"
            onClick={handleCreate}
          >
            添加团队成员
          </Button>
        </Permission>
        <Permission service={['choerodon.code.project.cooperation.team-member.ps.import']}>
          <Button
            icon="archive"
            onClick={handleImportRole}
          >
            导入团队成员
          </Button>
        </Permission>
        {getInitialButton()}
      </Header>
      <Breadcrumb />
      <DeleteRoleModal
        deleteRoleRecord={deleteRoleRecord}
        handleCancel={handleCancel}
        projectId={projectId}
      />
      <Content
        className="project-user"
      >
        <Table labelLayout="float" pristine dataSet={dataSet}>
          <Column renderer={renderUserName} name="realName" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="loginName" />
          <Column minWidth={320} width={320} renderer={expandMoreColumn} className="project-user-roles" name="myRoles" />
          <Column renderer={rednerEnabled} name="enabled" align="left" />
        </Table>
      </Content>
    </TabPage>
  );
}
