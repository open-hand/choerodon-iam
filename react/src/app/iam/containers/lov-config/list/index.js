import React, { PureComponent, useContext, useState } from 'react';
import { Content, Page, Header, Breadcrumb, Action, axios } from '@choerodon/boot';
import { Modal, Table, Icon, Tooltip, Button, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from '../stores';

import Sider from './Sider';
import Preview from './preview';
import './style/index.less';


const { Column } = Table;

export default observer(() => {
  const context = useContext(Store);
  const { intl, lovDataSet, permissions } = context;

  async function openModal(isCreate) {
    if (isCreate) {
      lovDataSet.create();
    }
    Modal.open({
      title: isCreate ? '创建LOV' : '修改LOV',
      drawer: true,
      style: { width: 'calc(100% - 3.5rem)' },
      children: <Sider context={context} />,
    });
    if (!isCreate) {
      const result = await axios.get(`/iam/choerodon/v1/lov/code?code=${lovDataSet.current.get('code')}`);
      lovDataSet.children.gridFields.loadData(result.gridFields);
      lovDataSet.children.queryFields.loadData(result.queryFields);
    }
  }

  function handlePreview(record) {
    Modal.open({
      title: '预览LOV',
      drawer: true,
      style: { width: '3.8rem' },
      children: <Preview context={context} lovCode={record.get('code')} />,
    });
  }

  function handleDelete(record) {
    Modal.confirm({
      title: '确认删除LOV',
      children: `确认删除LOV"${record.get('code')}"吗？`,
      onOk: async () => {
        try {
          const res = await axios.delete(`/iam/choerodon/v1/lov/${record.get('id')}`);
          if (res.failed) {
            throw res.message;
          } else {
            message.info('删除成功');
          }
        } catch (err) {
          message.error(err);
        } finally {
          lovDataSet.query();
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
    }, {
      service: [],
      text: '预览',
      action: () => handlePreview(record),
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
        <Button icon="playlist_add" onClick={openModal}>创建lov</Button>
      </Header>
      <Breadcrumb />

      <Content className="c7n-lov">
        <div className="c7n-pro-lov-table">
          <Table
            pristine
            dataSet={lovDataSet}
            border={false}
            queryBar="bar"
          >
            <Column renderer={renderCode} name="code" />
            <Column renderer={renderAction} width={50} align="right" />
            <Column className="text-gray" label="描述" name="description" />
            <Column className="text-gray" name="resourceLevel" />
          </Table>
        </div>
      </Content>
    </Page>
  );
});
