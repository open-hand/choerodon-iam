import React from 'react';
import { observer } from 'mobx-react-lite';
import { NumberField, Form, SelectBox, TextField, Password, Icon, Tooltip } from 'choerodon-ui/pro';
import { Modal } from 'choerodon-ui';

import './index.less';

const { Option } = SelectBox;
const { Sidebar } = Modal;
export default observer(({ dataSet, onOk, onCancel, prefixCls }) => {
  function handleCancel() {
    onCancel();
    dataSet.reset();
  }
  async function handleOk() {
    if (await dataSet.submit()) {
      await dataSet.query();
      await onOk();
    }
  }
  return (
    <Sidebar
      title="添加客户端"
      bodyStyle={{ padding: '0 0.2rem' }}
      okCancel
      okText="保存"
      onOk={handleOk}
      onCancel={handleCancel}
      visible
      width={390}
    >
      <Form className="hidden-password" dataSet={dataSet}>
        <input type="password" style={{ position: 'absolute', top: '-999px' }} />
        <TextField name="name" style={{ marginTop: 15 }} />
        <Password name="secret" />
        <SelectBox name="authorizedGrantTypes" multiple>
          <Option value="password">password</Option>
          <Option value="implicit">implicit</Option>
          <Option value="client_credentials">client_credentials</Option>
          <Option value="authorization_code">authorization_code</Option>
          <Option value="refresh_token">refresh_token</Option>
        </SelectBox>
        <NumberField name="accessTokenValidity" suffix="秒" />
        <NumberField name="refreshTokenValidity" suffix="秒" />
      </Form>
      <div className="organization-pwdpolicy-label">
        <span>是否进行加密</span>
        <Tooltip title="用此客户端调用接口查询，返回结果是否进行主键加密" placement="top">
          <Icon type="help" />
        </Tooltip>
      </div>
      <Form dataSet={dataSet}>
        <SelectBox name="apiEncryptFlag">
          <Option value={1}>
            <span className="organization-pwdpolicy-box-span">是</span>
          </Option>
          <Option value={0}>
            <span className="organization-pwdpolicy-box-span">否</span>
          </Option>
        </SelectBox>
      </Form>
    </Sidebar>
  );
});
