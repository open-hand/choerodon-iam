import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Output, Modal, Password, TextField, Icon, Progress } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import StatusTag from '../../../../components/statusTag';
import './OrganizationLdap.less';

const LdapTestForm = observer(({ ldapDataSet, ldapTestDataSet, modal, orgId }) => {
  const [isPanelShow, showPanel] = useState(false);
  const { current } = ldapTestDataSet;
  const password = current && current.get('password');
  const account = current && current.get('account');
  const ldapId = ldapDataSet.current && ldapDataSet.current.get('id');

  useEffect(() => {
    if (!ldapTestDataSet.current) {
      ldapTestDataSet.create();
    }
  });

  async function handleTest() {
    if (!ldapTestDataSet.current) {
      return false;
    }
    if (!await ldapTestDataSet.current.validate()) {
      return false;
    }
    if (ldapTestDataSet.current.validate()) {
      try {
        const result = await axios.post(`/base/v1/organizations/${orgId}/ldaps/test_connect`, { account, password });
        if (result) {
          showPanel(true);
        }
        const { canLogin, canConnectServer, matchAttribute } = result;
        if (current) {
          current.set('canLogin', canLogin);
          current.set('canConnectServer', canConnectServer);
          current.set('matchAttribute', matchAttribute);
        }
        return false;
      } catch (e) {
        return false;
      }
    }
  }
  // 取消测试ldap
  function handleTestCancel() {
    ldapTestDataSet.reset();
    return true;
  }

  modal.handleOk(handleTest);
  modal.handleCancel(handleTestCancel);

  // 渲染字段
  function renderText(value) {
    const style = { marginLeft: '.2rem' };
    if (value) {
      return <StatusTag style={style} name="成功" colorCode="COMPLETED" />;
    } else {
      return <StatusTag style={style} name="失败" colorCode="FAILED" />;
    }
  }

  return (
    <div>
      <Form dataSet={ldapTestDataSet} labelLayout="float" className="hidden-password">
        <input type="password" style={{ position: 'absolute', top: '-999px' }} />
        <TextField label="管理员登录名" name="account" required />
        <Password label="管理员密码" name="password" required />
      </Form>
      <div style={{ display: isPanelShow ? 'block' : 'none' }} className="c7n-organization-LdapTest-panel">
        <div className="c7n-organization-LdapTest-panel-content">
          <Form dataSet={ldapTestDataSet} labelLayout="horizontal" style={{ flex: '1' }}>
            <Output name="canLogin" label="LDAP登录" renderer={({ value }) => renderText(value)} />
            <Output name="canConnectServer" label="基础链接" renderer={({ value }) => renderText(value)} />
            <Output name="matchAttribute" label="用户属性链接" renderer={({ value }) => renderText(value)} />
          </Form>
        </div>
        <Form dataSet={ldapDataSet} className="no-star" labelLayout="horizontal" style={{ marginLeft: '0.2rem' }}>
          <Output name="loginNameField" />
          <Output name="emailField" />
          <Output name="realNameField" />
          {ldapDataSet.current.get('phoneField') && <Output name="phoneField" renderer={({ text }) => (text || '此属性暂未设置')} />}
          {ldapDataSet.current.get('uuidField') && <Output name="uuidField" showHelp="none" />}
        </Form>
      </div>
    </div>
  );
});

export default LdapTestForm;
