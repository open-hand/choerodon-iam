import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage, Choerodon } from '@choerodon/boot';
import { Button } from 'choerodon-ui';
import _ from 'lodash';
import { Select, SelectBox, Table, TextField, Modal, message } from 'choerodon-ui/pro';
import expandMoreColumn from '../../../components/expandMoreColumn';
import StatusTag from '../../../components/statusTag';
import Store from './stores';
import Sider from './sider';
import './index.less';

const modalKey = Modal.key();

const { Column } = Table;
export default function ListView(props) {
  const { intlPrefix,
    permissions,
    intl,
    userListDataSet: dataSet,
    userRoleDataSet,
    allRoleDataSet,
    orgRoleDataSet,
    organizationDataSet,
  } = useContext(Store);
  const modalProps = {
    modify: {
      okText: '保存',
      title: '修改平台用户',
    },
    roleAssignment: {
      okText: '确定',
      title: '添加平台用户',
    },
    importRole: {
      okText: '返回',
      okCancel: false,
      title: '导入平台用户',
    },
  };
  async function handleDisable(record) {
    try {
      await axios.put(`/iam/choerodon/v1/users/${record.get('id')}/disable`);
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
      await axios.put(`/iam/choerodon/v1/users/${record.get('id')}/enable`);
      await dataSet.query();
    } catch (err) {
      Choerodon.prompt(err);
    }
  }
  async function handleUnLock(record) {
    try {
      await axios.put(`/iam/choerodon/v1/users/${record.get('id')}/unlock`);
      await dataSet.query();
    } catch (err) {
      Choerodon.prompt(err);
    }
  }

  function handleSave() {
    dataSet.query();
  }
  function openModal(type) {
    Modal.open({
      ...modalProps[type],
      children: <Sider
        orgRoleDataSet={orgRoleDataSet}
        allRoleDataSet={allRoleDataSet}
        type={type}
        userRoleDataSet={userRoleDataSet}
        userListDataSet={dataSet}
        onOk={handleSave}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      fullScreen: true,
      destroyOnClose: true,
      className: 'base-site-user-sider',
    });
  }
  function handleModify(record) {
    dataSet.current = record;
    openModal('modify');
  }
  function handleRoleAssignment() {
    openModal('roleAssignment');
  }
  function handleImportRole() {
    openModal('importRole');
  }
  const queryOrg = _.debounce((str) => {
    organizationDataSet.setQueryParameter('organization_name', str);
    if (str !== '') { organizationDataSet.query(); }
  }, 500);
  function handleSearchOrganization(e) {
    e.persist();
    queryOrg(e.target.value);
  }
  function renderLocked({ value }) {
    return value ? '锁定' : '';
  }
  function rednerEnabled({ value }) {
    return <StatusTag name={value ? '启用' : '停用'} colorCode={value ? 'COMPLETED' : 'DEFAULT'} />;
  }
  // function getQueryFields() {
  //   return {
  //     realName: <TextField clearButton labelLayout="float" />,
  //     roleName: <Select dropdownMenuStyle={{ width: 260 }} labelLayout="float" />,
  //     loginName: <TextField clearButton labelLayout="float" />,
  //     enabled: <SelectBox mode="button" />,
  //     locked: <SelectBox mode="button" />,
  //     organizationId: <Select onInput={handleSearchOrganization} dropdownMenuStyle={{ width: 260 }} labelLayout="float" searchable />,
  //   };
  // }
  function renderAction({ record }) {
    const actionDatas = [];
    if (record.get('enabled')) {
      actionDatas.push({
        text: <FormattedMessage id={`${intlPrefix}.action.disable`} />,
        action: () => handleDisable(record),
      });
    } else {
      actionDatas.push({
        text: <FormattedMessage id={`${intlPrefix}.action.enable`} />,
        action: () => handleEnable(record),
      });
    }
    if (record.get('locked')) {
      actionDatas.push({
        text: <FormattedMessage id={`${intlPrefix}.action.unlock`} />,
        action: () => handleUnLock(record),
      });
    }
    return <Action data={actionDatas} />;
  }

  function rendeRealName({ record, text }) {
    return <span className="link" onClick={() => handleModify(record)}>{text}</span>;
  }

  return (
    <TabPage>
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Button icon="person_add" onClick={handleRoleAssignment}><FormattedMessage id={`${intlPrefix}.button.assign-roles`} /></Button>
        <Button icon="archive" onClick={handleImportRole}>导入平台用户</Button>
      </Header>
      <Breadcrumb />
      <Content
        className="site-user"
      >
        <Table labelLayout="float" pristine dataSet={dataSet}>
          <Column renderer={rendeRealName} name="realName" width={150} />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="loginName" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="organizationName" />
          <Column renderer={rednerEnabled} name="enabled" width={80} align="left" />
          <Column minWidth={320} width={320} renderer={expandMoreColumn} className="site-user-roles" name="myRoles" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} width={100} renderer={renderLocked} name="locked" />
        </Table>
      </Content>
    </TabPage>
  );
}
