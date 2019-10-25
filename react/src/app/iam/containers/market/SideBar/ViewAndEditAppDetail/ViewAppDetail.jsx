import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { DataSet, Table, Form, Output, Spin, NumberField, Modal, Button, Tabs } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import classnames from 'classnames';
import './ViewAppDeatil.less';
import Store from './Store';

const { Column } = Table;

const cssPrefix = 'c7n-market-viewAppDetail';

const ImgContainer = ({ value }) => (
  <img src={value} alt="appIcon" className={`${cssPrefix}-img`} />
);

const publishTypeContainer = ({ value }) => {
  switch (value) {
    case 'mkt_code_only':
      return (<span>源代码</span>);
    case 'mkt_deploy_only':
      return (<span>部署包</span>);
    case 'mkt_code_deploy':
      return (<span>源代码、部署包</span>);
    default:
  }
};

const IsFreeContainer = ({ value }) => <span>{value ? '是' : '否'}</span>;

const HTMLContainer = ({ value }) => (<p className={`${cssPrefix}-htmlContainer`} dangerouslySetInnerHTML={{ __html: value }} style={{ marginBottom: 0 }} />);

const ViewAppDetail = observer((props) => {
  const { viewAndEditAppDetailDataSet, status } = useContext(Store);
  return (
    <Spin dataSet={viewAndEditAppDetailDataSet}>
      <Form dataSet={viewAndEditAppDetailDataSet} labelLayout="horizontal" labelAlign="left" className={`${cssPrefix}-form`} labelWidth={150}>
        <Output name="imageUrl" renderer={ImgContainer} />
        <Output name="name" />
        <Output name="contributor" />
        <Output name="notificationEmail" />
        <Output name="categoryName" />
        <Output name="description" />
        <Output name="free" renderer={IsFreeContainer} />
        <Output name="publishType" renderer={publishTypeContainer} />
        <Output name="overview" renderer={HTMLContainer} />
        {viewAndEditAppDetailDataSet.current && viewAndEditAppDetailDataSet.current.get('approveMessage') ? <Output name="approveMessage" /> : null}
      </Form>
    </Spin>
  );
});
export default ViewAppDetail;
