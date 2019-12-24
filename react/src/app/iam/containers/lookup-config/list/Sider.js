import React, { Component, useState } from 'react';
import { SelectBox, message, Table, TextField, Form, Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Action } from '@choerodon/boot';

const { Column } = Table;
export default observer(({ modal, context }) => {
  const { lookupDataSet } = context;
  const lookupValueDataSet = lookupDataSet.children.lookupValues;
  async function handleOk() {
    try {
      if (!await lookupDataSet.submit()) {
        return false;
      }
    } catch (err) {
      return false;
    }
  }
  modal.handleOk(handleOk);
  modal.handleCancel(() => lookupDataSet.reset());
  function renderAction({ record }) {
    const actionDatas = [];
    actionDatas.push({
      service: [],
      text: '删除',
      action: () => lookupValueDataSet.delete(record),
    });
    return (
      <Action data={actionDatas} />
    );
  }
  function create() {
    lookupDataSet.children.lookupValues.create();
  }
  return (
    <React.Fragment>
      <div className="c7n-lookup-sider-form">
        <Form dataSet={lookupDataSet}>
          <TextField name="code" />
          <TextField name="description" />
        </Form>
      </div>
      <div className="c7n-lookup-sider-tableTop">
        <span className="c7n-lookup-sider-tableTitle">选项信息</span>
        <Button icon="playlist_add" color="primary" funcType="flat" className="c7n-lookup-sider-tableButton" onClick={create}>添加选项</Button>
      </div>
      <Table dataSet={lookupDataSet.children.lookupValues} queryBar="none">
        <Column name="code" editor={<TextField />} />
        <Column renderer={renderAction} width={50} />
        <Column name="description" editor />
        <Column name="displayOrder" align="left" editor />
      </Table>
    </React.Fragment>
    
  );
});
