import React, { useContext, Fragment, useState, useCallback } from 'react';
import { Choerodon, Content, Header, Page, Permission, TabPage, axios, Breadcrumb } from '@choerodon/boot';
import { Form, Output, Modal, TextField, Password } from 'choerodon-ui/pro';
import { Button } from 'choerodon-ui';
import { withRouter } from 'react-router';
import './OrganizationLdap.less';
import { observer } from 'mobx-react-lite';
import LdapForm from './LdapForm';
import LdapTestForm from './LdapTestForm';
import Sider from './Sider';

import Store from '../stores';

const modalKey = Modal.key();
const LdapView = observer(() => {
  const {
    orgId,
    ldapDataSet, 
    ldapTestDataSet, 
    ldapLoadClientDataSet, 
    match, 
    history,
    intl,
  } = useContext(Store);
  const { current: currentRecord } = ldapDataSet;
  const ldapId = currentRecord && currentRecord.get('id');

  // 根据传入的btns类型去返回对应的组件,title,text,onOk
  function renderFormType(type) {
    switch (type) {
      case 'isModify':
        return {
          children: <LdapForm orgId={orgId} dataSet={ldapDataSet} />,
          okText: '保存并测试连接',
          title: '修改设置',
          destroyOnClose: true,
        };
      case 'isTestLinks':
        return {
          children: <LdapTestForm orgId={orgId} ldapDataSet={ldapDataSet} ldapTestDataSet={ldapTestDataSet} />,
          okText: '测试',
          title: '测试连接',
          destroyOnClose: true,
        };
      case 'loaderClient':
        return {
          children: <Sider orgId={orgId} ldapId={ldapDataSet.current.get('id')} />,
          okText: '同步',
          title: '同步用户',
          destroyOnClose: true,
        };
      default:
        return '';
    }
  }
  // type：给modal传个值知道是那个按钮
  function openModal(btnType) {
    const modalProps = renderFormType(btnType, ldapDataSet);
    Modal.open({
      ...modalProps,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      fullScreen: true,
      cancelText: '取消',
    });
  }
  // 修改信息
  function modifyInfo() {
    openModal('isModify');
  }
  // 测试链接
  function testLinks() {
    openModal('isTestLinks');
  }
  // 同步客户
  function loaderClient() {
    openModal('loaderClient');
  }
  // 跳转到同步记录的界面
  function goToHistory() {
    history.push(`${match.url}/sync-record${history.location.search}`);
  }
  async function handleDisableLdap() {
    try {
      await axios.put(`/base/v1/organizations/${orgId}/ldaps/${ldapId}/disable`);
      await ldapDataSet.query();
    } catch (e) {
      Choerodon.prompt(e);
    }
  }
  function handleDisable() {
    Modal.open({
      key: modalKey,
      title: '停用LDAP',
      children: (
        <div>
          <p>{intl.formatMessage({ id: 'organization.ldap.disable.content' })}</p>
        </div>
      ),
      onOk: handleDisableLdap,
    });
  }
  async function handleEnableLdap() {
    try {
      const result = await axios.put(`/base/v1/organizations/${orgId}/ldaps/${ldapId}/enable`);
      if (result.failed) {
        throw result.message;
      }
      await ldapDataSet.query();
    } catch (e) {
      Choerodon.prompt(e);
    }
  }
  return (
    <TabPage
      service={[
        'base-service.ldap.queryByOrgId',
        'base-service.ldap.update',
        'base-service.ldap.disableLdap',
        'base-service.ldap.enableLdap',
        'base-service.ldap.testConnect',
        'base-service.ldap.syncUsers',
        'base-service.ldap.pagingQueryHistories',
      ]}
    >
      <Header>
        <Permission service={['base-service.ldap.update']}>
          <Button type="primary" funcType="flat" icon="mode_edit" onClick={modifyInfo}>修改</Button>
        </Permission>
        <Permission service={['base-service.ldap.disableLdap']}>
          {currentRecord && currentRecord.getPristineValue('enabled') ? <Button type="primary" funcType="flat" icon="remove_circle_outline" onClick={handleDisable}>停用</Button> : ''}
        </Permission>
        <Permission service={['base-service.ldap.enableLdap']}>
          {currentRecord && !currentRecord.getPristineValue('enabled') ? <Button type="primary" funcType="flat" icon="check_circle" onClick={handleEnableLdap}>启用</Button> : ''}
        </Permission>
        <Permission service={['base-service.ldap.testConnect']}>
          <Button type="primary" funcType="flat" icon="low_priority" onClick={testLinks}>测试连接</Button>
        </Permission>
        <Permission service={['base-service.ldap.syncUsers']}>
          <Button type="primary" funcType="flat" icon="sync_user" onClick={loaderClient}>同步用户</Button>
        </Permission>
        <Permission service={['base-service.ldap.pagingQueryHistories']}>
          <Button type="primary" funcType="flat" icon="sync_records" onClick={goToHistory}>同步记录</Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className="c7n-organization-page-content">
        <div className="c7n-pro-organization-LdapForm-container">
          <h3>服务器设置</h3>
          <Form pristine header="" dataSet={ldapDataSet} className="c7n-pro-organization-LdapForm" labelLayout="horizontal" labelWidth={235} labelAlign="left">
            <Output
              name="directoryType"
              renderer={({ text }) => (text || '未设置目录类型')}
            />
            <Output name="serverAddress" />
            <Output
              name="useSSL"
              renderer={({ text }) => (text ? '是' : '否')}
            />
            <Output name="port" />
            <Output name="sagaBatchSize" />
            <Output
              name="connectionTimeout"
              renderer={({ text }) => (text ? `${text}秒` : '暂未设置时间')}
            />
            <Output name="baseDn" showHelp="none" />
            <Output name="account" showHelp="none" />
            <Output
              name="password"
              renderer={({ text }) => (text ? <span style={{ fontWeight: 700 }}>··················</span> : <span>此用户暂未设置密码</span>)}
            />
          </Form>
        </div>
        <div className="c7n-pro-organization-LdapForm-container">
          <h3>用户属性设置</h3>
          <Form pristine dataSet={ldapDataSet} className="c7n-pro-organization-LdapForm" labelLayout="horizontal" labelWidth={235} labelAlign="left">
            <Output name="objectClass" showHelp="none" />
            <Output name="loginNameField" />
            <Output name="emailField" />
            <Output name="realNameField" />
            <Output name="phoneField" renderer={({ text }) => text || '此属性暂未设置'} />
            <Output name="uuidField" showHelp="none" />
            <Output name="customFilter" showHelp="none" />
          </Form>
        </div>
      </Content>
    </TabPage>
  );
});
export default withRouter(LdapView);
