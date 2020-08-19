import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Icon, Button, Row, Col, Form, Output,
} from 'choerodon-ui/pro';
import {
  Content, Header, Permission, TabPage, Breadcrumb,
} from '@choerodon/boot';
import Store from '../store';
import EditPassword from './editPassword';

export default observer(() => {
  const { passwordPolicyDataSet } = useContext(Store);
  const [visible, setVisible] = useState(false);
  function openPasswordModal() {
    setVisible(true);
  }
  function renderBoolean({ value }) {
    return value ? '是' : '否';
  }

  function handleCancel() {
    setVisible(false);
  }
  function handleSave() {
    setVisible(false);
  }

  return (
    <TabPage service={['choerodon.code.organization.setting.security.ps.password-policy']}>
      <Header>
        <Permission service={['choerodon.code.organization.setting.security.ps.password-policy.update']}>
          <Button color="blue" onClick={openPasswordModal}>
            <Icon type="mode_edit" /> 修改安全策略
          </Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className="safe-content ml-15">
        <Form pristine dataSet={passwordPolicyDataSet} labelWidth={450} className="tab1">
          <Row>
            <Col span={24}>
              <span className="policyTitle">密码安全策略</span>
            </Col>
            <Col span={24}>
              <Row><label>是否启用</label><Output name="enablePassword" renderer={renderBoolean} /></Row>
              {passwordPolicyDataSet.current && passwordPolicyDataSet.current.getPristineValue('enablePassword') ? [
                <Row><label>登录时强制修改默认密码</label><Output name="forceModifyPassword" renderer={renderBoolean} /></Row>,
                <Row><label>是否允许密码与登录名相同</label><Output name="notUsername" renderer={renderBoolean} /></Row>,
                <Row><label>新用户默认密码</label><Output name="originalPassword" /></Row>,
                <Row><label>最小密码长度</label><Output name="minLength" /></Row>,
                <Row><label>最大密码长度</label><Output name="maxLength" /></Row>,
                <Row><label>最少数字数</label><Output name="digitsCount" /></Row>,
                <Row><label>最少小写字母数</label><Output name="lowercaseCount" /></Row>,
                <Row><label>最少大写字母数</label><Output name="uppercaseCount" /></Row>,
                <Row><label>密码正则</label><Output name="regularExpression" /></Row>,
              ] : null}
            </Col>
          </Row>
          <Row>
            <Col span={24}>
              <span className="policyTitle">登录安全策略</span>
            </Col>
            <Col span={24}>
              <Row><label>是否启用</label><Output name="enableSecurity" renderer={renderBoolean} /></Row>
              {passwordPolicyDataSet.current && passwordPolicyDataSet.current.getPristineValue('enableSecurity') ? [
                <Row><label>是否开启验证码</label><Output name="enableCaptcha" renderer={renderBoolean} /></Row>,
                <Row><label>输错次数</label><Output name="maxCheckCaptcha" /></Row>,
                <Row><label>是否开启锁定</label><Output name="enableLock" renderer={renderBoolean} /></Row>,
                <Row><label>输错次数</label><Output name="maxErrorTime" /></Row>,
                <Row><label>锁定时长</label><Output name="lockedExpireTime" /></Row>,
              ] : null}

            </Col>
          </Row>
        </Form>
        {visible && <EditPassword onCancel={handleCancel} onOk={handleSave} dataSet={passwordPolicyDataSet} />}
      </Content>
    </TabPage>
  );
});
