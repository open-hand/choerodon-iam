import React, { useContext, useState, use } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, TextField, Password, Select, EmailField } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import FormSelectEditor from '../../../../components/formSelectEditor';

const { Option } = Select;
export default observer((props) => {
  const { prefixCls, intlPrefix, modal, userListDataSet, onOk, allRoleDataSet, orgRoleDataSet } = useContext(Store);
  function handleCancel() {
    userListDataSet.reset();
  }
  async function handleOk() {
    if (!userListDataSet.current.dirty && !userListDataSet.current.get('dirty')) {
      return true;
    }
    if (await userListDataSet.submit()) {
      await userListDataSet.reset();
      await onOk();
      return true;
    } else {
      return false;
    }
  }
  modal.handleOk(handleOk);
  modal.handleCancel(handleCancel);

  function renderOption({ text, value }) {
    const result = allRoleDataSet.find(item => item.get('id') === value);
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
      <Form dataSet={userListDataSet}>
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
          record={userListDataSet.current}
          optionDataSet={orgRoleDataSet}
          name="roles"
          idField="id"
          addButton="添加其他角色"
          maxDisable
        >
          {((itemProps) => {
            const result = allRoleDataSet.find(item => item.get('id') === itemProps.value);
            return (
              <Select 
                {...itemProps}
                labelLayout="float"
                renderer={renderOption}
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
