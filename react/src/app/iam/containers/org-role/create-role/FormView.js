import React, { Fragment, useEffect, useMemo, useCallback } from 'react';
import { observer } from 'mobx-react-lite';
import { Choerodon } from '@choerodon/boot';
import { Icon, message } from 'choerodon-ui';
import { Table, Form, TextField, Button, Select } from 'choerodon-ui/pro';
import { useCreateRoleStore } from './stores';
import LoadingBar from '../../../components/loadingBar';

import './index.less';

const { Column } = Table;

const ListView = () => {
  const {
    menuDs,
    formDs,
    modal,
    refresh,
    prefixCls,
    level,
  } = useCreateRoleStore();
  
  const record = useMemo(() => formDs.current, [formDs.current]);
  const isModify = useMemo(() => formDs.current && formDs.current.status !== 'add', [formDs.current]);

  const handleOkRole = useCallback(async () => {
    const selectedRecords = menuDs.filter((eachRecord) => eachRecord.get('isChecked'));
    if (!selectedRecords.length) {
      message.error('至少包含一个权限。');
      return false;
    }
    try {
      if (await formDs.submit() !== false) {
        refresh();
      } else {
        return false;
      }
    } catch (e) {
      Choerodon.handleResponseError(e);
      return false;
    }
  }, [menuDs.selected]);

  useEffect(() => {
    modal.handleOk(handleOkRole);
  }, [handleOkRole]);

  function renderName({ record: tableRecord }) {
    const { icon, name } = tableRecord.toData();
    return (
      <Fragment>
        <Icon
          type={icon}
          style={{ marginRight: '.08rem', lineHeight: '.32rem', verticalAlign: 'top' }}
        />
        {name}
      </Fragment>
    );
  }

  function renderType({ value, record: tableRecord }) {
    const permissionType = {
      api: 'API',
      button: '按钮',
    };
    return tableRecord.get('type') === 'ps' ? permissionType[value] || '' : '';
  }

  if (!record) {
    return <LoadingBar />;
  }

  return (
    <div className={`${prefixCls}`}>
      <Form
        style={{ width: '5.12rem' }}
        record={record}
        columns={2}
        className="c7n-role-msg-form"
      >
        <TextField name="code" disabled={isModify} />
        <TextField name="name" />
        {level === 'project' && <Select name="roleLabels" />}
      </Form>
      <div className={`${prefixCls}-menu`}>
        <span className={`${prefixCls}-menu-text`}>菜单分配</span>
      </div>
      <Table
        dataSet={menuDs}
        queryBar="none"
        mode="tree"
        buttons={[
          ['collapseAll', { icon: 'expand_less', children: '全部收起' }],
          ['expandAll', { icon: 'expand_more', children: '全部展开' }],
        ]}
        expandIconColumnIndex={1}
        className={`${prefixCls}-table`}
      >
        <Column name="isChecked" editor width={50} />
        <Column name="name" renderer={renderName} width={400} />
        <Column name="permissionType" renderer={renderType} />
      </Table>
    </div>
  );
};

export default observer(ListView);
