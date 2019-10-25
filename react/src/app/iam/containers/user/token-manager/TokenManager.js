import React, { Component, useEffect } from 'react';
import { observer, inject } from 'mobx-react-lite';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Breadcrumb, Content, Header, Page, Permission, axios, Choerodon } from '@choerodon/boot';
import { Button, Tooltip, Modal, Dropdown, Menu } from 'choerodon-ui';
import { Table } from 'choerodon-ui/pro';
import './TokenManager.less';
import TimeAgo from 'timeago-react';
import timeago from 'timeago.js';
import { Link, withRouter } from 'react-router-dom';
import MouseOverWrapper from '../../../components/mouseOverWrapper';
import StatusTag from '../../../components/statusTag';
import { useStore } from './stores';


// timeago.register('zh_CN', require('./locale/zh_CN'));
const { Column } = Table;
function TokenManager(props) {
  const context = useStore();
  const { tokenManagerDataSet, intl, intlPrefix } = context;

  function deleteRecords(records) {
    const currentToken = Choerodon.getAccessToken().split(' ')[1];
    const recordTokens = records.map(v => v.get('tokenId'));
    return axios.delete(`/base/v1/token/batch?currentToken=${currentToken}`, { data: recordTokens });
  }
  function deleteTokenById(tokenId) {
    const currentToken = Choerodon.getAccessToken().split(' ')[1];
    return axios.delete(`/base/v1/token?tokenId=${tokenId}&currentToken=${currentToken}`);
  }
  const handleDelete = (record) => {
    Modal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: intl.formatMessage({ id: `${intlPrefix}.remove.title` }),
      content: intl.formatMessage({ id: `${intlPrefix}.remove.content` }, { name: record.get('accesstoken') }),
      onOk: () => deleteTokenById(record.get('tokenId'), Choerodon.getAccessToken().split(' ')[1]).then(({ failed, message }) => {
        if (failed) {
          Choerodon.prompt(message);
        } else {
          Choerodon.prompt(intl.formatMessage({ id: 'remove.success' }));
          tokenManagerDataSet.query(tokenManagerDataSet.length === 1 ? tokenManagerDataSet.currentPage - 1 : tokenManagerDataSet.currentPage);
        }
      }),
    });
  };
  const renderDropDown = ({ text, action, disabled }) => {
    const menu = (
      <Menu onClick={action}>
        <Menu.Item key="1">
          {text}
        </Menu.Item>
      </Menu>
    );
    return (
      !disabled ? (
        <Dropdown overlay={menu} disabled={disabled} trigger={['click']}>
          <Button size="small" shape="circle" style={{ color: '#000' }} icon="more_vert" />
        </Dropdown>
      ) : null
    );
  };
  const renderAction = ({ record }) => {
    const action = {
      disabled: record.get('currentToken'),
      text: <FormattedMessage id="delete" />,
      action: () => handleDelete(record),
    };
    return renderDropDown(action);
  };

  const handleBatchDelete = () => {
    const selectedNum = tokenManagerDataSet.currentSelected.length;
    Modal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: intl.formatMessage({ id: `${intlPrefix}.remove.batch.title` }),
      content: intl.formatMessage({ id: `${intlPrefix}.remove.batch.content` }, { name: tokenManagerDataSet.currentSelected.length }),
      onOk: () => deleteRecords(tokenManagerDataSet.currentSelected).then(({ failed, message }) => {
        if (failed) {
          Choerodon.prompt(message);
        } else {
          Choerodon.prompt(intl.formatMessage({ id: 'remove.success' }));
          tokenManagerDataSet.query(selectedNum >= tokenManagerDataSet.length ? tokenManagerDataSet.currentPage - 1 : tokenManagerDataSet.currentPage);
        }
      }),
    });
  };

  const renderTime = time => (
    <Tooltip
      title={time}
      placement="top"
    >
      <TimeAgo
        datetime={time}
        locale={Choerodon.getMessage('zh_CN', 'en')}
      />
    </Tooltip>
  );

  const renderAccesstoken = ({ value, record }) => (
    <React.Fragment>
      <MouseOverWrapper style={{ float: 'left' }} text={value} width={0.15}>
        {value}
      </MouseOverWrapper>
      {
        record.get('currentToken')
          ? (
            <span className="c7n-iam-token-manager-current">当前</span>
          ) : null
      }
    </React.Fragment>
  );
  const renderRedirectUri = ({ value }) => (
    <MouseOverWrapper text={value} width={0.2}>
      <a className="c7n-iam-token-manager-url" onClick={() => window.open(value)}>{value}</a>
    </MouseOverWrapper>
  );
  const renderExpire = ({ value }) => (
    <StatusTag
      style={{ width: 40 }}
      mode="tags"
      name={!value ? '正常' : '已失效'}
      colorCode={!value ? 'COMPLETED' : 'DEFAULT'}
    />
  );
  function render() {
    const { permissions } = context;
   
    return (
      <Page
        service={permissions}
        className="c7n-iam-token-manager"
      >
        <Header>
          <Button onClick={handleBatchDelete} icon="delete_forever" disabled={tokenManagerDataSet.currentSelected.length === 0}>
            <FormattedMessage id="delete.all" />
          </Button>
        </Header>
        <Breadcrumb />
        <Content className="c7n-iam-token-manager-content">
          <Table dataSet={tokenManagerDataSet}>
            <Column name="accesstoken" renderer={renderAccesstoken} />
            <Column name="action" width={50} renderer={renderAction} />
            <Column name="clientId" width={100} />
            <Column name="redirectUri" renderer={renderRedirectUri} />
            <Column name="createTime" align="left" width={150} renderer={({ value }) => renderTime(value)} />
            <Column name="expirationTime" width={150} renderer={({ value }) => renderTime(value)} />
            <Column name="expire" width={100} align="left" renderer={renderExpire} />
          </Table>
        </Content>
      </Page>
    );
  }
  return render();
}
export default withRouter(observer(TokenManager));
