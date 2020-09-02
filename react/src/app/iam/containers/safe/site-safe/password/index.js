import React, { useState, useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Modal, Icon, Button, Form, Row, Col, Output,
} from 'choerodon-ui/pro';
import {
  Content, Header, Page, axios, Action, Permission, TabPage, Breadcrumb,
} from '@choerodon/boot';

import { FormattedMessage } from 'react-intl';
import Store from '../store';
import Sider from './editPassword';
import './index.less';

const modalKey = Modal.key();

export default observer(() => {
  const { systemSettingDataSet: dataSet } = useContext(Store);
  function openPasswordModal() {
    Modal.open({
      children: <Sider
        dataSet={dataSet}
      />,
      title: '修改密码策略',
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      fullScreen: true,
      destroyOnClose: true,
      className: 'site-password-sider',
    });
  }

  function renderBoolean({ value }) {
    return value ? '是' : '否';
  }

  return (
    <TabPage service={['choerodon.code.site.setting.security.ps.password-policy']}>
      <Header>
        <Permission service={['choerodon.code.site.setting.security.ps.password-policy.update']}>
          <Button color="blue" onClick={openPasswordModal}>
            <Icon type="mode_edit" />
            修改密码策略
          </Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className="site-password-content ml-15">
        <Form pristine dataSet={dataSet} labelLayout="horizontal" labelAlign="left" labelWidth={250}>
          {/* <Output name="forceModifyPassword" renderer={renderBoolean} /> */}
          <Output name="defaultPassword" />
          <Output name="minPasswordLength" />
          <Output name="maxPasswordLength" />
        </Form>
      </Content>
    </TabPage>
  );
});
