import React, { useContext, useState, useEffect, useMemo } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Form, TextField, Password, Select, EmailField, Modal } from 'choerodon-ui/pro';
import Store from './stores';
import FormSelectEditor from '../../../../components/formSelectEditor';
import './index.less';

export default observer(() => {
  const {
    prefixCls,
    modal,
    orgUserListDataSet,
    orgUserCreateDataSet,
    organizationId,
    orgRoleDataSet,
    onOk,
    userStore,
  } = useContext(Store);
  const addonAfterObj = useMemo(() => ({
    addonAfter: userStore.getEmailSuffix || undefined,
  }), [userStore.getEmailSuffix]);

  useEffect(() => {
    userStore.loadEmailSuffix(organizationId);
    return () => {
      userStore.setEmailSuffix(null);
    };
  }, []);
  
  async function handleOk() {
    try {
      if (await orgUserCreateDataSet.submit()) {
        orgUserCreateDataSet.reset();
        onOk();
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  }

  modal.handleOk(() => handleOk());
  modal.handleCancel(() => {
    orgUserCreateDataSet.reset();
  });

  return (
    <div className={`${prefixCls}-modal`}>
      <Form dataSet={orgUserCreateDataSet} className="hidden-password">
        <input type="password" style={{ position: 'absolute', top: '-999px' }} />
        <TextField name="realName" />
        <TextField name="email" {...addonAfterObj} />
        <Password name="password" />
      </Form>
      <FormSelectEditor
        record={orgUserCreateDataSet.current}
        optionDataSet={orgRoleDataSet}
        name="roles"
        addButton="添加其他角色"
        alwaysRequired
        canDeleteAll={false}
        maxDisable
      >
        {((itemProps) => (
          <Select 
            {...itemProps}
            labelLayout="float"
            style={{ width: '100%' }}
          />
        ))}
      </FormSelectEditor>
    </div>
  );
});
