import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Form, TextField, Password, Select, EmailField, Modal, Button } from 'choerodon-ui/pro';

export default observer((props) => {
  const { prefixCls, modal, langCreateDataSet, langListDataSet } = props;
  async function handleOk() {
    try {
      if (await langCreateDataSet.submit()) {
        await langCreateDataSet.reset();
        await langListDataSet.query();
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  }
  
  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    langCreateDataSet.reset();
  });

  return (
    <div>
      <Form dataSet={langCreateDataSet}>
        <TextField name="promptCode" />
        <Select name="lang" />
        <Select name="serviceCode" />
        <TextField name="description" />
      </Form>
    </div>
  );
});
