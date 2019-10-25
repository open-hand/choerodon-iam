import React, { PureComponent, useContext, useState } from 'react';
import { Content, Page, Header, Breadcrumb, Action } from '@choerodon/boot';
import { Modal, Table, TextField, Icon, Tooltip, TextArea } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from '../stores';

import StatusTag from '../../../components/statusTag';
import Sider from './Sider';
import './style/index.less';


const { Column } = Table;
const modalKey = Modal.key();

export default observer(() => {
  const { intl, approvalDataSet, permissions } = useContext(Store);
  function handleOpenModal(record) {
    approvalDataSet.current = record;
    Modal.open({
      key: modalKey,
      drawer: true,
      title: `审批“${record.get('userName')}”的注册申请`,
      style: { width: 380 },
      className: 'c7n-approval-sider',
      children: (
        <Sider intl={intl} record={record} />
      ),
      okCancel: record.getPristineValue('approvalStatus') === 'no_approval',
      fullScreen: true,
      okText: record.getPristineValue('approvalStatus') === 'no_approval' ? '提交' : '关闭',
    });
  }
  function renderStatus(record) {
    return (
      <StatusTag colorCode={record.value.toUpperCase()} name={intl.formatMessage({ id: `register.approval.${record.value}` })} style={{ lineHeight: '16px', width: '42px' }} />
    );
  }
  function renderAction({ record }) {
    const actionDatas = [];
    if (record.get('approvalStatus') === 'no_approval') {
      actionDatas.push({
        service: [permissions[1]],
        text: '审批',
        action: () => handleOpenModal(record),
      });
    } else {
      return;
    }
    return (
      <Action data={actionDatas} />
    );
  }

  function renderName({ text, record }) {
    if (record.get('approvalStatus') === 'no_approval') {
      return <span className="text-gray">{text}</span>;
    }
    return <span className="link" onClick={() => handleOpenModal(record)}>{text}</span>;
  }

  return (
    <Page
      service={permissions}
    >
      <Breadcrumb />

      <Content className="c7n-approval">
        <div className="c7n-pro-approval-table">
          <div className="c7n-pro-base-table-content">
            <Table
              pristine
              dataSet={approvalDataSet}
              border={false}
              queryBar="bar"
            >
              <Column renderer={renderName} name="userName" />
              <Column renderer={renderAction} width={50} align="right" />
              <Column name="approvalStatus" renderer={renderStatus} />
              <Column className="text-gray" name="userEmail" />
              <Column className="text-gray" name="userPhone" />
              <Column className="text-gray" name="orgEmailSuffix" />
              <Column className="text-gray" name="registerDate" />
              
            </Table>
          </div>
        </div>
      </Content>
    </Page>
  );
});
