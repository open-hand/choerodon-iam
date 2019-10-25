import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { Spin, Table, Form, Output, TextField, NumberField, Modal, Button, Tabs } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import classnames from 'classnames';
import './ViewVersionDetail.less';
import ReactMarkdown from 'react-markdown';
import Store from './Store';
import ExpandMoreColumn from '../../component/expandMoreColumn';

const versionStatusMap = {
  failure: 'error',
  done: 'success',
  // processing 有动画，会导致显示不全
  processing: 'default',
  unpublished: 'warning',
};

const { Column } = Table;

const cssPrefix = 'c7n-market-viewVersionDetail';

const ImgContainer = ({ value }) => (
  <img src={value} alt="appIcon" className={`${cssPrefix}-img`} />
);

const TableContainer = () => {
  const { serviceTableDataSet } = useContext(Store);
  return (
    <Table dataSet={serviceTableDataSet} queryBar="none" className={`${cssPrefix}-table`}>
      <Column name="name" width={150} />
      <Column
        name="appServiceVersions"
        className={`${cssPrefix}-serviceContainer`}
        header="应用服务版本"
        width={300}
        renderer={(renderProps) => (
          <ExpandMoreColumn
            {...renderProps}
            maxLength={362}
            nameField="version"
            showBadge={(service, index, fromPopOver) => (index > 0 || fromPopOver) && service.status !== 'done'}
            badgeConfig={(service, index) => ({
              status: versionStatusMap[service.status],
              offset: [8, 0],
            })}
          />
        )}
      />
    </Table>
  );
};

const HTMLContainer = ({ value }) => (<p className={`${cssPrefix}-htmlContainer`} dangerouslySetInnerHTML={{ __html: value }} style={{ marginBottom: 0 }} />);

const ChangelogContainer = ({ value }) => <ReactMarkdown source={value} />;

const TextContainer = ({ value }) => (value ? <span>{value}</span> : '暂无内容');

const ViewVersionDetail = observer((props) => {
  const { viewAndEditVersionDetailDataSet, status } = useContext(Store);
  return (
    <Spin dataSet={viewAndEditVersionDetailDataSet}>
      <Form dataSet={viewAndEditVersionDetailDataSet} labelLayout="horizontal" labelAlign="left" className={`${cssPrefix}-form`} labelWidth={150}>
        {status !== 'published' && <Output name="imageUrl" renderer={ImgContainer} /> }
        <Output name="version" />
        <Output renderer={() => <TableContainer />} label="包含的应用服务" />
        {status !== 'published' && <Output name="description" />}
        {status !== 'published' && <Output name="overview" renderer={HTMLContainer} />}
        <Output name="changelog" renderer={ChangelogContainer} />
        <Output name="document" renderer={HTMLContainer} />
        {status !== 'published' && status !== 'unconfirmed' && <Output name="remark" />}
        {viewAndEditVersionDetailDataSet.current && viewAndEditVersionDetailDataSet.current.get('approveMessage') ? <Output name="approveMessage" /> : null}
      </Form>
    </Spin>
  );
});
export default ViewVersionDetail;
