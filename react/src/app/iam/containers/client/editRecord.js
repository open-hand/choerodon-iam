import React from 'react';
import { observer } from 'mobx-react-lite';
import { NumberField, Form, SelectBox, TextArea, TextField, Password } from 'choerodon-ui/pro';
import { Modal } from 'choerodon-ui';

const { Option } = SelectBox;
const { Sidebar } = Modal;
export default observer(({ dataSet, onOk, onCancel }) => {
  const { current } = dataSet;
  function handleCancel() {
    onCancel();
    dataSet.reset();
  }
  async function handleOk() {
    if (!current.dirty) {
      onOk();
    }
    if (await dataSet.submit()) {
      await dataSet.query();
      await onOk();
    }
  }
  return (
    <Sidebar
      title="修改客户端"
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
        <SelectBox name="authorizedGrantTypes">
          <Option value="password">password</Option>
          <Option value="implicit">implicit</Option>
          <Option value="client_credentials">client_credentials</Option>
          <Option value="authorization_code">authorization_code</Option>
          <Option value="refresh_token">refresh_token</Option>
        </SelectBox>
        <TextField name="webServerRedirectUri" />
        <NumberField name="accessTokenValidity" suffix="秒" />
        <NumberField name="refreshTokenValidity" suffix="秒" />
        <TextField name="scope" showHelp="tooltip" />
        <TextField name="autoApprove" showHelp="tooltip" />
        <TextArea name="additionalInformation" resize="vertical" />
      </Form>
    </Sidebar>
  );
});
