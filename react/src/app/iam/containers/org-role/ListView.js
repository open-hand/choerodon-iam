import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, Header, axios, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Tag, Menu, Dropdown, Icon } from 'choerodon-ui';
import { Table, Modal } from 'choerodon-ui/pro';
import Store from './stores';
import FormView from './FormView';
import './index.less';

const { Column } = Table;

const LEVEL_MAP = {
  site: '全局',
  organization: '组织',
  project: '项目',
};
const modalKey = Modal.key();
const modalStyle = {
  width: 'calc(100% - 3.5rem)',
};

const ListView = () => {
  const context = useContext(Store);
  const { listDataSet: dataSet, level, setLevel } = context;

  function refresh() {
    dataSet.query();
  }

  function handleCancel() {
    const { current } = dataSet;
    if (current.status === 'add') {
      dataSet.remove(current);
    } else {
      current.reset();
    }
  }

  function openModal(base) {
    Modal.open({
      key: modalKey,
      drawer: true,
      title: dataSet.current.status === 'add' ? '创建角色' : '修改角色',
      children: (
        <FormView context={context} level={level} base={base} />
      ),
      style: modalStyle,
      onCancel: handleCancel,
    });
  }

  function handleCreateRole() {
    dataSet.create();
    openModal();
  }

  function handleClickBaseCreateRole() {
    dataSet.create();
    openModal(dataSet.selected.map((r) => r.get('id')));
  }

  function handleCreateByRecord(record) {
    dataSet.create();
    openModal([record.get('id')]);
  }

  function handleClickLevel(e) {
    setLevel(e.key);
  }

  async function handleEnabled() {
    const record = dataSet.current;
    const enabled = record.get('enabled');
    const res = await axios.put(`/base/v1/roles/${record.get('id')}/${enabled ? 'disable' : 'enable'}`);
    if (!res.failed) {
      dataSet.query();
    }
  }

  function renderLevelSelect() {
    const menu = (
      <Menu onClick={handleClickLevel}>
        <Menu.Item key="site">全局</Menu.Item>
        <Menu.Item key="project">项目</Menu.Item>
        <Menu.Item key="organization">组织</Menu.Item>
      </Menu>
    );
    return (
      <Dropdown overlay={menu} trigger={['click']}>
        <Button>
          {LEVEL_MAP[level]} <Icon type="arrow_drop_down" />
        </Button>
      </Dropdown>
    );
  }

  function renderAction({ record }) {
    const enabled = record.get('enabled');
    const actionDatas = [
      {
        service: [],
        text: '基于该角色创建',
        action: () => handleCreateByRecord(record),
      },
      {
        service: [],
        text: '修改',
        action: openModal,
      },
      {
        service: [],
        text: enabled ? '停用' : '启用',
        action: handleEnabled,
      },
    ];
    if (enabled && record.get('builtIn')) {
      actionDatas.splice(2, 1);
    }
    return <Action data={actionDatas} />;
  }

  function renderBuildIn({ record }) {
    return record.get('builtIn') ? '预定义' : '自定义';
  }

  function renderEnabled({ record }) {
    const enabled = record.get('enabled');
    return <Tag color={enabled ? '#00bfa5' : '#d3d3d3'}>{enabled ? '启用' : '停用'}</Tag>;
  }

  return (
    <TabPage service={[]}>
      <Header>
        {renderLevelSelect()}
        <Button icon="playlist_add" onClick={handleCreateRole}>创建角色</Button>
        <Button icon="playlist_add" onClick={handleClickBaseCreateRole} disabled={dataSet.selected.length === 0}>基于所选角色创建</Button>
        <Button icon="refresh" onClick={refresh}>刷新</Button>
      </Header>
      <Breadcrumb />
      <Content style={{ paddingTop: 0 }}>
        <Table dataSet={dataSet}>
          <Column name="name" width={200} />
          <Column renderer={renderAction} width={50} />
          <Column name="code" />
          <Column name="builtIn" renderer={renderBuildIn} width={150} />
          <Column name="enabled" renderer={renderEnabled} width={150} />
        </Table>
      </Content>
    </TabPage>
  );
};

export default observer(ListView);
