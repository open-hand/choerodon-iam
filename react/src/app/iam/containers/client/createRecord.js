import React from 'react';
import { observer } from 'mobx-react-lite';
import { NumberField, Form, SelectBox, TextField, Password } from 'choerodon-ui/pro';
import { Modal } from 'choerodon-ui';

const { Option } = SelectBox;
const { Sidebar } = Modal;
export default observer(({ dataSet, onOk, onCancel }) => {
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
      className="safe-modal"
    >
      <Form className="safe-modal-form hidden-password" dataSet={dataSet}>
        <input type="password" style={{ position: 'absolute', top: '-999px' }} />
        <TextField name="name" />
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
    </Sidebar>
  );
});
