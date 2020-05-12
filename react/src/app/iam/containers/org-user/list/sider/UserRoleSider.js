import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, Choerodon } from '@choerodon/boot';
import { Form, Modal, TextField, Select, EmailField } from 'choerodon-ui/pro';
import Store from './stores';
import FormSelectEditor from '../../../../components/formSelectEditor';
import './index.less';

const { Option } = Select;

export default observer((props) => {
  const { prefixCls, modal, orgUserRoleDataSet, onOk, organizationId, orgRoleDataSet, orgAllRoleDataSet, orgUserListDataSet } = useContext(Store);
  const { current } = orgUserRoleDataSet;
  function handleCancel() {
    orgUserRoleDataSet.reset();
  }
  async function handleOk() {
    const requestData = current.toJSONData();
    requestData.roles = requestData.roles.filter((v) => v).map((v) => ({ id: v }));
    // if (requestData.roles.length === 0) return false;
    const result = await axios.put(`/iam/choerodon/v1/organizations/${organizationId}/users/${current.toData().id}/assign_roles`, requestData.roles);
    if (!result.failed) {
      await orgUserRoleDataSet.reset();
      await onOk();
    } else {
      Choerodon.prompt(result.message);
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
    <div className={`${prefixCls}-modal`}>
      <Form disabled dataSet={orgUserListDataSet}>
        <TextField name="realName" />
        <EmailField name="email" />
        <TextField name="phone" />
        <Select value="zh_CN" label="语言">
          <Option value="zh_CN">简体中文</Option>
        </Select>
        <Select value="CTT" label="时区">
          <Option value="CTT">中国</Option>
        </Select>
      </Form>
      <FormSelectEditor
        record={orgUserRoleDataSet.current}
        optionDataSet={orgRoleDataSet}
        name="roles"
        addButton="添加其他角色"
        maxDisable
      >
        {((itemProps) => (
          <Select
            {...itemProps}
            labelLayout="float"
            renderer={renderOption}
            searchable
            style={{ width: '100%' }}
          />
        ))}
      </FormSelectEditor>
    </div>
  );
});
