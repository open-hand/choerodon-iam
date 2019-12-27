import React, { PureComponent, useContext, useState } from 'react';
import { Content, Page, Header, Breadcrumb, Action, axios } from '@choerodon/boot';
import { Modal, Table, Icon, Tooltip, Button, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from '../stores';

import StatusTag from '../../../components/statusTag';
import Sider from './Sider';
import './style/index.less';


const { Column } = Table;

export default observer(() => {
  const context = useContext(Store);
  const { intl, lookupDataSet, permissions } = context;

  async function openModal(isCreate) {
    if (isCreate) {
      lookupDataSet.create();
    }
    Modal.open({
      title: isCreate ? '添加快码' : '修改快码',
      drawer: true,
      style: { width: '7.2rem' },
      children: <Sider context={context} />,
    });
    if (!isCreate) {
      const result = await axios.get(`/base/v1/lookups/${lookupDataSet.current.get('id')}`);
      lookupDataSet.children.lookupValues.loadData(result.lookupValues);
    }
  }

  function handleDelete(record) {
    Modal.confirm({
      title: '确认删除快码',
      children: `确认删除快码"${record.get('code')}"吗？`,
      onOk: async () => {
        try {
          const res = await axios.delete(`/base/v1/lookups/${record.get('id')}`);
          if (res.failed) {
            throw res.message;
          } else {
            message.info('删除成功');
          }
        } catch (err) {
          message.error(err);
        } finally {
          lookupDataSet.query();
        }
      },
    });
  }

  function renderAction({ record }) {
    const actionDatas = [];
    actionDatas.push({
      service: [],
      text: '删除',
      action: () => handleDelete(record),
    });
    return (
      <Action data={actionDatas} />
    );
  }

  function renderCode({ text, record }) {
    return <span className="link" onClick={() => openModal(false)}>{text}</span>;
  }

  return (
    <Page
      service={permissions}
    >
      <Header>
        <Button icon="playlist_add" onClick={openModal}>创建lookup</Button>
      </Header>
      <Breadcrumb />

      <Content className="c7n-lookup">
        <div className="c7n-pro-lookup-table">
          <Table
            pristine
            dataSet={lookupDataSet}
            border={false}
            queryBar="bar"
          >
            <Column renderer={renderCode} name="code" />
            <Column renderer={renderAction} width={50} align="right" />
            <Column className="text-gray" name="description" />
          </Table>
        </div>
      </Content>
    </Page>
  );
});
