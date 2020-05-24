import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, Output, Modal, Password, TextField, Icon, Progress } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';
import { extractFieldValueFromDataSet } from './Sider/LdapLoadClient';
import StatusTag from '../../../../components/statusTag';

const LdapTestForm = observer(({ userInfo, modal, dataSet, orgId }) => {
  const ldapId = extractFieldValueFromDataSet(dataSet, 'id');
  const [isPanelShow, changeStatus] = useState(false);
  const [isLoading, changeLoadingStatus] = useState(false);
  const [canConnectServer, changeConnectStatus] = useState(false);
  const [canLogin, changeLoginStatus] = useState(false);
  const [matchAttribute, changeMatchStatus] = useState(false);

  function postData() {
    changeStatus(false);
    changeLoadingStatus(true);
    axios.post(`/iam/v1/${orgId}/ldaps/${ldapId}/test-connect`, userInfo).then((res) => {
      if (res) {
        changeLoadingStatus(false);
        changeConnectStatus(res.canConnectServer);
        changeLoginStatus(res.canLogin);
        changeMatchStatus(res.matchAttribute);
        changeStatus(true);
      }
    });
    return false;
  }
  useEffect(() => {
    changeLoadingStatus(true);
    postData();
  }, []);

  // 渲染字段
  function renderText(value) {
    const style = { marginLeft: '.2rem' };
    if (value) {
      return <StatusTag style={style} name="成功" colorCode="COMPLETED" />;
    } else {
      return <StatusTag style={style} name="失败" colorCode="FAILED" />;
    }
  }
  modal.handleOk(() => postData());
  return (
    <div>
      <div style={{ display: isPanelShow ? 'block' : 'none' }} className="c7n-organization-LdapTest-panel">
        <ul className="c7n-organization-LdapFormToTest-lists">
          <li><span>LDAP登录</span><span>{renderText(canConnectServer)}</span></li>
          <li><span>基础链接</span><span>{renderText(canLogin)}</span></li>
          <li><span>用户属性链接</span><span>{renderText(matchAttribute)}</span></li>
        </ul>
        <ul className="c7n-organization-LdapFormToTest-lists c7n-organization-LdapFormToTest-lists-padding">
          <li><span>登录名属性</span><span>{userInfo.loginNameField}</span></li>
          <li><span>邮箱属性</span><span>{userInfo.emailField}</span></li>
          {userInfo.realNameField && <li><span>用户名属性</span><span>{userInfo.realNameField}</span></li>}
          {userInfo.phoneField && <li><span>手机号属性</span><span>{userInfo.phoneField}</span></li>}
          <li><span>uuid属性</span><span>{userInfo.uuidField}</span></li>

        </ul>
      </div>
      <div className="c7n-organization-LdapTest-panel-loading" style={{ display: isLoading ? 'block' : 'none' }}>
        <div>
          <Progress type="loading" size="large" />
          <h1 style={{ marginTop: '.2rem' }}>正在测试中</h1>
        </div>
      </div>
    </div>
  );
});

export default LdapTestForm;
