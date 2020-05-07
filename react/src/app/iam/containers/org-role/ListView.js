import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, Header, axios, Breadcrumb, Page } from '@choerodon/boot';
import { Button, Tag } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import Store from './stores';
import FormView from './create-role';

import './index.less';

const { Column } = Table;

const modalKey = Modal.key();
const modalStyle = {
  width: 740,
};

const ListView = () => {
  const context = useContext(Store);
  const {
    intl: { formatMessage },
    listDataSet: dataSet,
    prefixCls,
  } = context;

  function handleCancel() {
    const { current } = dataSet;
    if (current.status === 'add') {
      dataSet.remove(current);
    } else {
      current.reset();
    }
  }

  function openModal(type, level) {
    const record = dataSet.current;
    Modal.open({
      key: modalKey,
      drawer: true,
      title: type === 'add' ? '创建角色' : '修改角色',
      children: (
        <FormView level={level} roleId={type === 'edit' ? record.get('id') : null} />
      ),
      style: modalStyle,
      onCancel: handleCancel,
    });
  }

  async function handleEnabled() {
    const record = dataSet.current;
    const enabled = record.get('enabled');
    const res = await axios.put(`/base/v1/roles/${record.get('id')}/${enabled ? 'disable' : 'enable'}`);
    if (!res.failed) {
      dataSet.query();
    }
  }

  function handleDelete() {
    const record = dataSet.current;
    const modalProps = {
      title: '删除角色',
      children: '确定删除该角色吗？',
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
    };
    dataSet.delete(record, modalProps);
  }

  function renderAction({ record }) {
    const enabled = record.get('enabled');
    const builtIn = record.get('builtIn');
    const actionDatas = [
      {
        service: [],
        text: '修改',
        action: () => openModal('edit', record.get('level')),
      },
      {
        service: [],
        text: enabled ? '停用' : '启用',
        action: handleEnabled,
      },
      {
        service: [],
        text: '删除',
        action: handleDelete,
      },
    ];
    return !builtIn && <Action data={actionDatas} />;
  }

  function renderBuildIn({ value }) {
    return value ? '预定义' : '自定义';
  }

  function renderEnabled({ value }) {
    return <Tag color={value ? '#00bfa5' : '#d3d3d3'}>{value ? '启用' : '停用'}</Tag>;
  }

  function renderLevel({ value }) {
    return value === 'project' ? '项目层' : '组织层';
  }

  return (
    <Page service={[]}>
      <Header>
        <Button icon="playlist_add" onClick={() => openModal('add', 'organization')}>创建组织角色</Button>
        <Button icon="playlist_add" onClick={() => openModal('add', 'project')}>创建项目角色</Button>
      </Header>
      <Breadcrumb />
      <Content className={`${prefixCls}`}>
        <Table dataSet={dataSet}>
          <Column name="name" width={200} />
          <Column renderer={renderAction} width={50} />
          <Column name="code" />
          <Column name="level" renderer={renderLevel} width={150} />
          <Column name="builtIn" renderer={renderBuildIn} width={150} align="left" />
          <Column name="enabled" renderer={renderEnabled} width={150} align="left" />
        </Table>
      </Content>
    </Page>
  );
};

export default observer(ListView);
