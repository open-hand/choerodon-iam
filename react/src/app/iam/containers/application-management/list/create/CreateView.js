import React, { useContext, useState } from 'react';
import { FormattedMessage, injectIntl } from 'react-intl';
import { Action, Content, Header, axios, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Table, Form, TextField, TextArea } from 'choerodon-ui/pro';
import Store from './stores';
import '../index.less';

const { Column } = Table;
export default function CreateView() {
  const { intlPrefix, permissions, intl, context, prefixCls, sharedServiceDataSet, projectServiceDataSet, modal, mode } = useContext(Store);
  const { applicationDataSet, serviceTypeMap } = context;
  
  function renderType({ value }) {
    return serviceTypeMap[value];
  }
  modal.handleOk(async () => {
    if (mode === 'create') {
      const selectedShared = sharedServiceDataSet.selected.map(record => record.get('id'));
      const selectedProject = projectServiceDataSet.selected.map(record => record.get('id'));
      applicationDataSet.current.set('serviceIds', selectedShared.concat(selectedProject));
    }
    try {
      if (await applicationDataSet.submit()) {
        applicationDataSet.query();
      } else {
        if (applicationDataSet.current.dirty === false) {
          return true;
        }
        return false;
      }
    } catch (err) {
      return false;
    }
  });
  modal.handleCancel(() => {
    applicationDataSet.reset();
  });
  function getForm() {
    return (
      <Form dataSet={applicationDataSet}>
        <TextField name="name" />
        <TextArea resize="vertical" name="description" />
      </Form>
    );
  }
  function renderUpdate() {
    return (<div style={{ padding: '0 .2rem' }}> {getForm()} </div>);
  }
  function renderCreate() {
    return (<div className="form-content"> {getForm()} </div>);
  }
  function getServiceTable(dataSet) {
    return (
      <Table dataSet={dataSet}>
        <Column name="name" />
        <Column name="code" />
        <Column renderer={renderType} name="type" />
      </Table>
    );
  }
  function renderService() {
    return (
      <React.Fragment>
        <div className="service-title">选择应用服务</div>
        <div className="table-content">
          <div className="service-title-small">本项目下的服务</div>
          {getServiceTable(projectServiceDataSet)}
        </div>
        <div className="table-content">
          <div className="service-title-small">共享服务</div>
          <Table dataSet={sharedServiceDataSet}>
            <Column name="name" />
            <Column name="code" />
          </Table>
        </div>
      </React.Fragment>
    );
  }
  return (
    <React.Fragment>
      { mode === 'update' && renderUpdate()}
      { mode === 'create' && renderCreate()}
      { mode === 'create' && renderService()}
      
    </React.Fragment>
  );
}
