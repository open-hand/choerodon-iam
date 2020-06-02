import React, { useContext, useState, useEffect } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage, Choerodon } from '@choerodon/boot';
import { Button } from 'choerodon-ui';
import _ from 'lodash';
import { Select, SelectBox, Table, TextField, Modal, message } from 'choerodon-ui/pro';
import Store from './stores';
import Sider from './sider';
import './index.less';

const modalKey = Modal.key();

const { Column } = Table;
export default function ListView(props) {
  const { intlPrefix, intl, AppState, langListDataSet, langCreateDataSet } = useContext(Store);
  const modalProps = {
    modify: {
      okText: '保存',
      title: '修改',
    },
    create: {
      okText: '保存',
      title: '添加多语言维护',
    },
  };
  async function handleDelete(record) {
    try {
      await axios.delete(`/iam/choerodon/v1/prompt/${record.get('id')}`);
      const result = await langListDataSet.query();
      if (result.failed) {
        throw result.message;
      }
    } catch (err) {
      message.error(err);
    }
  }

  function handleSave() {
    langListDataSet.query();
  }
  function openModal(type) {
    Modal.open({
      ...modalProps[type],
      children: <Sider
        type={type}
        onOk={handleSave}
        langCreateDataSet={langCreateDataSet}
        langListDataSet={langListDataSet}
      />,
      key: modalKey,
      drawer: true,
      style: { width: 380 },
      destroyOnClose: true,
      className: 'base-lang-sider',
    });
  }
  function handleModify(record) {
    langListDataSet.current = record;
    openModal('modify');
  }
  function handleCreate() {
    openModal('create');
  }
  function renderAction({ record }) {
    const actionDatas = [
      {
        text: '删除',
        action: () => handleDelete(record),
      },
    ];
    return <Action data={actionDatas} />;
  }

  function rendeRealName({ record, text }) {
    return <span className="link" onClick={() => handleModify(record)}>{text}</span>;
  }

  return (
    <TabPage>
      <Header
        title={<FormattedMessage id={`${intlPrefix}.header.title`} />}
      >
        <Button icon="playlist_add" onClick={handleCreate}>新增</Button>
      </Header>
      <Breadcrumb />
      <Content className="lang-config">
        <Table labelLayout="float" pristine dataSet={langListDataSet}>
          <Column name="promptCode" renderer={rendeRealName} width={150} />
          <Column renderer={renderAction} width={50} align="right" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="lang" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="description" />
          <Column style={{ color: 'rgba(0, 0, 0, 0.65)' }} name="serviceCode" />
        </Table>
      </Content>
    </TabPage>
  );
}
