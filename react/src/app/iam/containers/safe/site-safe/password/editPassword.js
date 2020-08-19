import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Form, Password, SelectBox, NumberField, Select,
} from 'choerodon-ui/pro';

const { Option } = Select;

export default observer(({ dataSet, modal }) => {
  const { current } = dataSet;
  async function handleOk() {
    if (!current.dirty) {
      return true;
    }
    try {
      if (await dataSet.submit()) {
        await dataSet.query();
        return true;
      }
      return false;
    } catch (err) {
      return false;
    }
  }
  function handleCancel() {
    dataSet.reset();
  }
  modal.handleOk(handleOk);
  modal.handleCancel(handleCancel);

  return (
    <div
      className="safe-modal"
    >
      <Form columns={2} className="safe-modal-form hidden-password" dataSet={dataSet}>
        <SelectBox name="forceModifyPassword" label="登录时强制修改默认密码" colSpan={2}>
          <Option value key="yes">是</Option>
          <Option value={false} key="no">否</Option>
        </SelectBox>
        <input colSpan={2} type="password" style={{ position: 'absolute', top: '-999px' }} />
        <Password colSpan={2} name="defaultPassword" />
        <NumberField name="minPasswordLength" />
        <NumberField name="maxPasswordLength" />
      </Form>
    </div>
  );
});
