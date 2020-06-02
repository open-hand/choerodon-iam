import React, { useRef, useEffect } from 'react';
import { Form, TextField, Tooltip, Select, SelectBox, Output, Password, NumberField, Modal, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import LdapFormToTest from './LdapFormToTest';
import './OrganizationLdap.less';

const modalKey = Modal.key();
const { Option } = Select;
const InfoForm = observer(({ dataSet, modal, orgId }) => {
  const account = dataSet.current && dataSet.current.get('account');
  const ldapPassword = dataSet.current && dataSet.current.get('ldapPassword');
  const loginNameField = dataSet.current && dataSet.current.get('loginNameField');
  const emailField = dataSet.current && dataSet.current.get('emailField');
  const realNameField = dataSet.current && dataSet.current.get('realNameField');
  const phoneField = dataSet.current && dataSet.current.get('phoneField');
  const uuidField = dataSet.current && dataSet.current.get('uuidField');
  const useSSL = dataSet.current && dataSet.current.get('useSSL');
  const prevCountRef = useRef();
  const userInfo = {
    account, ldapPassword, loginNameField, emailField, realNameField, phoneField, uuidField,
  };
  function testAgain() {
    return true;
  }
  function openTestModal() {
    Modal.open({
      key: modalKey,
      drawer: true,
      destoryOnClose: true,
      title: '测试结果',
      style: { width: 380 },
      children: (
        <LdapFormToTest orgId={orgId} userInfo={userInfo} dataSet={dataSet} />
      ),
      fullScreen: true,
      onOk: testAgain,
      okText: '测试',
    });
  }
  // 保存信息并且测试
  async function handleSave() {
    try {
      if (!dataSet.current.dirty) {
        if (await dataSet.current.validate()) {
          openTestModal();
          return true;
        }
      }
      const result = await dataSet.submit();
      if (result) {
        await dataSet.query();
        openTestModal();
        return true;
      } else {
        throw result.message;
      }
    } catch (e) {
      return false;
    }
  }
  function handleCancel() {
    dataSet.reset();
  }

  modal.handleOk(handleSave);
  modal.handleCancel(handleCancel);

  function renderOption({ value, text }) {
    const tipMap = {
      OpenLDAP: '由OpenLDAP项目开发的轻量级目录访问协议（LDAP）的免费开源实现',
      'Microsoft Active Directory': '微软Windows Server中，负责架构中大型网络环境的集中式目录管理服务',
    };
    return (
      <Tooltip placement="left" title={tipMap[value]}>
        {text}
      </Tooltip>
    );
  }

  useEffect(() => {
    if (prevCountRef.current) {
      dataSet.current.set('port', useSSL ? 636 : 390);
    } else {
      prevCountRef.current = true;
    }
  }, [useSSL]);

  return (
    <div>
      <Form dataSet={dataSet} labelLayout="float" header="服务器设置">
        <Select optionRenderer={renderOption} name="directoryType" />
        <TextField label="主机名*" name="serverAddress" required />
        <SelectBox label="是否使用SSL*" name="useSSL" required>
          <Option value>是</Option>
          <Option value={false}>否</Option>
        </SelectBox>
        <TextField label="端口号" name="port" />
        <TextField label="同步用户saga每次发送用户的数量*" name="sagaBatchSize" required />
        <TextField
          label="ldap服务器连接超时时间*"
          name="connectionTimeout"
          required
          suffix={(
            <span style={{
              lineHeight: 1.5,
              color: '#666',
            }}
            >秒
            </span>
          )}
        />
        <TextField label="基准DN" name="baseDn" showHelp="tooltip" required />
        <TextField label="管理员登录名*" name="account" showHelp="tooltip" required />
        <input type="password" style={{ position: 'absolute', top: '-999px' }} />
        <Password label="管理员密码*" name="ldapPassword" required />
      </Form>
      <Form dataSet={dataSet} labelLayout="float" header="用户属性设置">
        <TextField label="用户对象类*" name="objectClass" showHelp="tooltip" required />
        <TextField label="登录名属性*" name="loginNameField" required />
        <TextField label="邮箱属性" name="emailField" />
        <TextField label="用户名属性*" name="realNameField" required />
        <TextField label="手机号属性*" name="phoneField" required />
        <TextField label="uuid属性" name="uuidField" showHelp="tooltip" required />
        <TextField label="自定义筛选用户条件*" name="customFilter" showHelp="tooltip" required />
      </Form>
    </div>
  );
});

export default InfoForm;
