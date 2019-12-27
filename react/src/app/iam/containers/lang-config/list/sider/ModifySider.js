import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Form, TextField, Password, Select, EmailField, Modal, Button } from 'choerodon-ui/pro';

export default observer((props) => {
  const { prefixCls, modal, langListDataSet } = props;
  async function handleOk() {
    if (!langListDataSet.current.dirty && !langListDataSet.current.get('dirty')) {
      return true;
    }
    if (await langListDataSet.submit()) {
      return true;
    } else {
      return false;
    }
  }
  
  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    langListDataSet.reset();
  });

  return (
    <div>
      <Form dataSet={langListDataSet}>
        <TextField name="promptCode" />
        <Select name="lang" />
        <Select name="serviceCode" />
        <TextField name="description" />
      </Form>
    </div>
  );
});
