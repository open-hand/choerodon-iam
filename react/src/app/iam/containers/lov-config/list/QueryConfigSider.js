import React, { Component, useState } from 'react';
import { SelectBox, Tooltip, Table, TextField, Form, Select, NumberField, Modal } from 'choerodon-ui/pro';
import { Action } from '@choerodon/boot';
import { observer } from 'mobx-react-lite';

export default observer(({ modal, queryFieldDataSet }) => {
  modal.handleCancel(() => {
    if (queryFieldDataSet.current) {
      queryFieldDataSet.current.reset();
    }
  });
  modal.handleOk(() => {
    
  });
  function renderHelpOption({ text, record }) {
    return (
      <Tooltip placement="left" title={record.get('description')}>{text}</Tooltip>
    );
  }
  return (
    <Form dataSet={queryFieldDataSet}>
      <Select name="queryFieldParamType" />
      <NumberField name="queryFieldWidth" />
      <NumberField name="queryFieldOrder" />
      <TextField name="queryFieldLabel" />
      <SelectBox name="queryFieldDisplayFlag" disabled={queryFieldDataSet.current.get('queryFieldParamType') === 'path'} />
      <SelectBox name="queryFieldRequiredFlag" disabled={queryFieldDataSet.current.get('queryFieldParamType') === 'path'} />
      <Select name="queryFieldType" />
      {queryFieldDataSet.current && queryFieldDataSet.current.get('queryFieldType') === 'Lov' 
        && <Select searchable searchMatcher="params" optionRenderer={renderHelpOption} name="queryFieldLovCode" />}
      {queryFieldDataSet.current && queryFieldDataSet.current.get('queryFieldType') === 'Select' 
        && <Select searchable searchMatcher="params" optionRenderer={renderHelpOption} name="queryFieldLookupCode" />}
    </Form>
  );
});
