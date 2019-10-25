import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Action, Content, Header, axios, Breadcrumb, TabPage } from '@choerodon/boot';
import { Button, Tag, Menu, Dropdown, Icon } from 'choerodon-ui';
import { Table, Modal, Select } from 'choerodon-ui/pro';
import Store from './stores';
import FormView from './FormView';
import './index.less';

const { Column } = Table;
const { Option } = Select;

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
    if (dataSet.current.status === 'add') {
      dataSet.current.set('labels', ['']);
    }
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

  function handleClickLevel(value) {
    setLevel(value);
  }

  async function handleEnabled() {
    const record = dataSet.current;
    const enabled = record.get('enabled');
    const res = await axios.put(`/base/v1/roles/${record.get('id')}/${enabled ? 'disable' : 'enable'}`);
    if (!res.failed) {
      dataSet.query();
    }
  }

  function renderTool() {
    return (
      <Select labelLayout="float" label="层级" clearButton={false} value={LEVEL_MAP[level]} onChange={handleClickLevel} style={{ width: '3.4rem', margin: '.16rem 0' }}>
        <Option key="site" value="site">全局</Option>
        <Option key="organization" value="organization">组织</Option>
        <Option key="project" value="project">项目</Option>
      </Select>
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
        text: enabled ? '停用' : '启用',
        action: handleEnabled,
      },
    ];
    if (enabled && record.get('builtIn')) {
      actionDatas.splice(1, 1);
    }
    return <Action data={actionDatas} />;
  }

  function renderBuildIn({ record }) {
    return record.get('builtIn') ? '预定义' : '自定义';
  }

  function renderEnabled({ record }) {
    const enabled = record.get('enabled');
    return (
      <div className="role-status-wrap" style={{ background: enabled ? '#00bfa5' : '#d3d3d3' }}>
        <div className="word">{enabled ? '启用' : '停用'}</div>
      </div>
    );
  }
  function renderName({ text }) {
    return <span onClick={openModal} className="link">{text}</span>;
  }

  return (
    <TabPage service={[]}>
      <Header>
        <Button icon="playlist_add" onClick={handleCreateRole}>创建角色</Button>
        <Button icon="playlist_add" onClick={handleClickBaseCreateRole} disabled={dataSet.selected.length === 0}>基于所选角色创建</Button>
      </Header>
      <Breadcrumb />
      <Content style={{ paddingTop: 0 }} className="role-table-sign">
        {renderTool()}
        <Table dataSet={dataSet}>
          <Column renderer={renderName} name="name" width={200} />
          <Column renderer={renderAction} width={50} />
          <Column name="code" />
          <Column name="builtIn" renderer={renderBuildIn} width={150} align="left" />
          <Column name="enabled" renderer={renderEnabled} width={150} align="left" />
        </Table>
      </Content>
    </TabPage>
  );
};

export default observer(ListView);
