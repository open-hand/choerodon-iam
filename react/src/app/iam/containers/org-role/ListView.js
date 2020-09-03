/* eslint-disable jsx-a11y/click-events-have-key-events, jsx-a11y/no-static-element-interactions */

import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import {
  Action, Content, Header, axios, Breadcrumb, Page, Permission, Choerodon,
} from '@choerodon/boot';
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
    AppState: { currentMenuType: { organizationId } },
    listDataSet: dataSet,
    prefixCls,
    permissions,
    intlPrefix,
  } = context;

  function refresh() {
    dataSet.query();
  }

  function openModal(type, level) {
    const record = dataSet.current;
    Modal.open({
      key: modalKey,
      drawer: true,
      title: type === 'add' ? '创建角色' : '修改角色',
      children: (
        <FormView
          level={level}
          roleId={type === 'edit' ? record.get('id') : null}
          refresh={refresh}
        />
      ),
      style: modalStyle,
    });
  }

  function openEnabledModal() {
    const record = dataSet.current;
    const enabled = record.get('enabled');
    if (enabled) {
      Modal.open({
        key: Modal.key(),
        title: formatMessage({ id: `${intlPrefix}.enable.title` }, { name: record.get('name') }),
        children: formatMessage({ id: `${intlPrefix}.enable.des` }),
        movable: false,
        onOk: () => handleEnabled(true),
      });
    } else {
      handleEnabled(false);
    }
  }

  async function handleEnabled(enabled) {
    const record = dataSet.current;
    const postData = record.toData();
    try {
      await axios.put(`/iam/hzero/v1/${organizationId}/roles/${enabled ? 'disable' : 'enable'}`, JSON.stringify(postData));
      dataSet.query();
    } catch (e) {
      Choerodon.handleResponseError(e);
    }
  }

  function handleDelete() {
    const record = dataSet.current;
    const modalProps = {
      title: formatMessage({ id: `${intlPrefix}.delete.title` }, { name: record.get('name') }),
      children: formatMessage({ id: `${intlPrefix}.delete.des` }),
      okText: formatMessage({ id: 'delete' }),
      okProps: { color: 'red' },
      cancelProps: { color: 'dark' },
    };
    dataSet.delete(record, modalProps);
  }

  function renderName({ value, record: tableRecord }) {
    if (tableRecord.get('builtIn')) {
      return <span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{value}</span>;
    }
    return (
      <Permission
        service={['choerodon.code.organization.manager.role.ps.update']}
        defaultChildren={(<span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{value}</span>)}
      >
        <span
          onClick={() => openModal('edit', tableRecord.get('roleLevel'))}
          className="link"
        >
          {value}
        </span>
      </Permission>
    );
  }

  function renderAction({ record }) {
    const enabled = record.get('enabled');
    const builtIn = record.get('builtIn');
    const actionDatas = [
      {
        service: [enabled ? 'choerodon.code.organization.manager.role.ps.disable' : 'choerodon.code.organization.manager.role.ps.enable'],
        text: enabled ? '停用' : '启用',
        action: openEnabledModal,
      },
    ];
    if (!enabled) {
      actionDatas.push({
        service: ['choerodon.code.organization.manager.role.ps.delete'],
        text: '删除',
        action: handleDelete,
      });
    }
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
    <Page service={permissions}>
      <Header>
        <Permission service={['choerodon.code.organization.manager.role.ps.create.organization']}>
          <Button
            icon="playlist_add"
            onClick={() => openModal('add', 'organization')}
          >
            创建组织角色
          </Button>
        </Permission>
        <Permission service={['choerodon.code.organization.manager.role.ps.create.project']}>
          <Button
            icon="playlist_add"
            onClick={() => openModal('add', 'project')}
          >
            创建项目角色
          </Button>
        </Permission>
      </Header>
      <Breadcrumb />
      <Content className={`${prefixCls}`}>
        <Table dataSet={dataSet}>
          <Column name="name" width={200} renderer={renderName} />
          <Column renderer={renderAction} width={50} />
          <Column name="code" style={{ color: 'rgba(0, 0, 0, 0.65)' }} />
          <Column name="roleLevel" renderer={renderLevel} width={150} style={{ color: 'rgba(0, 0, 0, 0.65)' }} />
          <Column name="builtIn" renderer={renderBuildIn} width={150} align="left" style={{ color: 'rgba(0, 0, 0, 0.65)' }} />
          <Column name="enabled" renderer={renderEnabled} width={150} align="left" />
        </Table>
      </Content>
    </Page>
  );
};

export default observer(ListView);
