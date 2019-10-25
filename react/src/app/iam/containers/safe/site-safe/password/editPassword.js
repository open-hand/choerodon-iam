import React, { useState } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Form, Output, Password, SelectBox, NumberField, TextField } from 'choerodon-ui/pro';

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
      } else {
        return false;
      }
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
        <input colSpan={2} type="password" style={{ position: 'absolute', top: '-999px' }} />
        <Password colSpan={2} name="defaultPassword" />
        <NumberField name="minPasswordLength" />
        <NumberField name="maxPasswordLength" />
      </Form> 
    </div>
  );
});
