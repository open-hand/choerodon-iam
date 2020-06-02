import React, { PureComponent, useContext, useState } from 'react';
import { Content, Page, Header, Breadcrumb, Action, axios } from '@choerodon/boot';
import { DataSet, Table, TextField, Icon, Tooltip, message, Modal } from 'choerodon-ui/pro';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import Store from '../../stores';
import Sider from './sider';
import './index.less';

const { Column } = Table;
const modalKey = Modal.key();
export default observer(() => {
  const { intl, organizationCategoryDataSet: dataSet, modalStyle, organizationFormListDataSet, projectFormListDataSet } = useContext(Store);
  function handleDetail(record) {
    Modal.open({
      key: modalKey,
      drawer: true,
      title: '查看类型',
      okCancel: false,
      okText: '关闭',
      className: 'org-category',
      children: (
        <Sider organizationFormListDataSet={organizationFormListDataSet} projectFormListDataSet={projectFormListDataSet} orgId={record.get('id')} listDataSet={dataSet} mode="detail" />
      ),
      style: modalStyle,
    });
  }
  async function handleDelete(record) {
    try {
      OldModal.confirm({
        className: 'c7n-iam-confirm-modal',
        title: '确认删除组织类型',
        content: `确认删除组织类型"${record.get('name')}"吗？`,
        onOk: async () => {
          try {
            await axios.delete(`/iam/choerodon/v1/categories/org/${record.get('id')}`);
            await dataSet.query();
          } catch (e) {
            return false;
          }
        },
      });
    } catch (error) {
      message.error(error);
    }
  }
  function renderAction({ record }) {
    const actionDatas = [];
    if (!record.get('builtInFlag')) {
      actionDatas.push({
        text: '删除',
        action: () => handleDelete(record),
      });
    }
    if (actionDatas.length === 0) {
      return '';
    }
    return (
      <Action data={actionDatas} />
    );
  }
  function renderBuiltInFlag({ value }) {
    return value ? '预定义' : '自定义';
  }
  function renderName({ text, record }) {
    return <span className="link" onClick={() => handleDetail(record)}>{text}</span>;
  }
  return (
    <Page>
      <Breadcrumb />
      <Content className="org-category">
        <Table
          dataSet={dataSet}
          border={false}
          queryBar="bar"
        >
          <Column renderer={renderName} name="name" />
          <Column renderer={renderAction} width={50} align="right" />
          <Column className="text-gray" name="code" />
          <Column className="text-gray" name="description" />
          <Column className="text-gray" renderer={renderBuiltInFlag} name="builtInFlag" />
        </Table>
      </Content>
    </Page>
  );
});
