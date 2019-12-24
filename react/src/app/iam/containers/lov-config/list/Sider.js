import React, { Component, useState } from 'react';
import { SelectBox, Tooltip, Table, TextField, Form, Select, NumberField, Modal, Button } from 'choerodon-ui/pro';
import { Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';
import QueryConfigSide from './QueryConfigSider';
import './style/index.less';

const { Column } = Table;
export default observer(({ modal, context }) => {
  const { lovDataSet } = context;
  const gridFieldDataSet = lovDataSet.children.gridFields;
  const queryFieldDataSet = lovDataSet.children.queryFields;
  async function handleOk() {
    try {
      if (!await lovDataSet.submit()) {
        return false;
      }
    } catch (err) {
      return false;
    }
  }
  modal.handleOk(handleOk);
  modal.handleCancel(() => lovDataSet.reset());

  function openQueryConfig() {
    queryFieldDataSet.current = queryFieldDataSet.find(r => r.get('queryFieldName') === gridFieldDataSet.current.get('gridFieldName'));
    if (!queryFieldDataSet.current) {
      queryFieldDataSet.current = queryFieldDataSet.create({ queryFieldName: gridFieldDataSet.current.get('gridFieldName') });
    }
    Modal.open({
      title: '查询配置',
      drawer: true,
      style: { width: 380 },
      children: <QueryConfigSide queryFieldDataSet={queryFieldDataSet} />,
    });
  }

  function renderAction({ record }) {
    const actionDatas = [];
    actionDatas.push({
      service: [],
      text: '删除',
      action: () => gridFieldDataSet.delete(record),
    });
    if (record.get('gridFieldQueryFlag')) {
      actionDatas.push({
        service: [],
        text: '查询配置',
        action: openQueryConfig,
      });
    }
    return (
      <Action data={actionDatas} />
    );
  }
  function renderHelpOption({ text, record }) {
    return (
      <Tooltip placement="left" title={record.get('description')}>{text}</Tooltip>
    );
  }

  return (
    <React.Fragment>
      <div className="c7n-lov-sider-form">
        <Form columns={4} dataSet={lovDataSet}>
          <TextField name="code" />
          <TextField name="description" label="placeholder" />
          <TextField colSpan={2} name="placeholder" showHelp="tooltip" />
          <Select name="resourceLevel" />
          <Select searchable searchMatcher="params" optionRenderer={renderHelpOption} name="permissionCode" />
          <TextField name="valueField" />
          <TextField name="textField" />
          <SelectBox name="editFlag" />
          <SelectBox name="multipleFlag" />
          <TextField newLine colSpan={2} name="title" />
          <NumberField name="height" />
          <NumberField name="width" />
          <SelectBox newLine name="treeFlag" />
          {lovDataSet.current && lovDataSet.current.get('treeFlag') && [
            <TextField name="idField" />,
            <TextField name="parentField" />,
          ]}
          <SelectBox newLine name="pageFlag" />
          {
            lovDataSet.current && lovDataSet.current.get('pageFlag') 
            && <NumberField name="pageSize" />
          }
        </Form>
      </div>
      <div className="c7n-lov-sider-table-title"><span>列信息</span><Button color="primary" onClick={() => gridFieldDataSet.create({})} icon="playlist_add">添加列</Button></div>
      <Table queryBar="none" dataSet={gridFieldDataSet}>
        <Column name="gridFieldName" editor />
        <Column renderer={renderAction} width={50} />
        <Column name="gridFieldLabel" editor />
        <Column name="gridFieldOrder" editor width={50} />
        <Column name="gridFieldAlign" editor width={120} />
        <Column name="gridFieldWidth" editor width={100} />
        <Column name="gridFieldDisplayFlag" editor width={100} />
        <Column name="gridFieldQueryFlag" editor width={130} />
      </Table>
    </React.Fragment>
    
  );
});
