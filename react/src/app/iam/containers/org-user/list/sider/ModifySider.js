import React, { useContext, useState, use } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Form, TextField, Password, Select, EmailField } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import FormSelectEditor from '../../../../components/formSelectEditor';

const { Option } = Select;
export default observer((props) => {
  const { prefixCls, intlPrefix, modal, orgUserListDataSet, onOk, orgAllRoleDataSet, orgRoleDataSet } = useContext(Store);
  function handleCancel() {
    orgUserListDataSet.reset();
  }
  async function handleOk() {
    if (!orgUserListDataSet.current.dirty && !orgUserListDataSet.current.get('dirty')) {
      return true;
    }
    if (await orgUserListDataSet.submit()) {
      await orgUserListDataSet.reset();
      await onOk();
      return true;
    } else {
      return false;
    }
  }
  modal.handleOk(handleOk);
  modal.handleCancel(handleCancel);
  function renderOption({ text, value }) {
    const result = orgAllRoleDataSet.find(item => item.get('id') === value);
    if (!result) {
      return `${value}`;
    }
    if (!result.get('enabled')) {
      return `${result && result.get('name')}（已停用）`;
    }
    return result && result.get('name');
  }

  return (
    <div
      className={`${prefixCls}-modal`}
    >
      <Form dataSet={orgUserListDataSet}>
        <TextField name="realName" />
        <EmailField name="email" />
        <TextField name="phone" />
        <Select value="zh_CN" label="语言">
          <Option value="zh_CN">简体中文</Option>
        </Select>
        <Select value="CTT" label="时区">
          <Option value="CTT">中国</Option>
        </Select>
        <FormSelectEditor
          record={orgUserListDataSet.current}
          optionDataSet={orgRoleDataSet}
          name="roles"
          idField="id"
          addButton="添加其他角色"
          maxDisable
        >
          {((itemProps) => {
            const result = orgAllRoleDataSet.find(item => item.get('id') === itemProps.value);
            return (
              <Select 
                {...itemProps}
                labelLayout="float"
                renderer={renderOption}
                searchable
                disabled={result && !result.get('enabled')}
                style={{ width: '100%' }}
              />
            );
          })}
        </FormSelectEditor>
      </Form>
    </div>
  );
});
