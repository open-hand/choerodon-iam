import React, { useContext, useState } from 'react';
import { Table, Form, TextField, TextArea } from 'choerodon-ui/pro';
import Store from './stores';
import '../index.less';

const { Column } = Table;
export default function CreateView() {
  const { intlPrefix, permissions, intl, context, prefixCls, sharedServiceDataSet, projectServiceDataSet, modal, mode } = useContext(Store);
  const { serviceDataSet: applicationDataSet, serviceTypeMap } = context;
  
  modal.handleOk(async () => {
    const selectedShared = sharedServiceDataSet.selected.map(record => record.get('id'));
    const selectedProject = projectServiceDataSet.selected.map(record => record.get('id'));
    applicationDataSet.current.set('serviceIds', selectedShared.concat(selectedProject));
    try {
      if (await applicationDataSet.submit()) {
        applicationDataSet.query();
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  });
  modal.handleCancel(() => {
    applicationDataSet.reset();
  });
  function renderType({ value }) {
    return serviceTypeMap[value];
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
        <div className="service-title mt-0">选择应用服务</div>
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
      { mode === 'add' && renderService()}
      
    </React.Fragment>
  );
}
