import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { Table, Form, TextField, Button, Tabs, Select, Tooltip, Icon, SelectBox, TextArea } from 'choerodon-ui/pro';
import { Collapse, message, Spin } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { axios, StatusTag } from '@choerodon/boot';
import './ReleaseNewVersion.less';
import Store from './Store';
import Editor from '../../../../components/editor';
import ChangeLogEditor from '../../../../components/ChangeLogEditor';
import colorMap from '../../../../utils/colorMap';

const { Column } = Table;

const { Panel } = Collapse;

const { TabPane } = Tabs;

const { Option } = Select;

const cssPrefix = 'c7n-market-releaseNewVersion';


const AppVersionContainer = ({ record }) => (record.get('appServiceVersions') ? (
  <span>{record.get('appServiceVersions')[0].version}</span>
) : <span>无版本</span>);

const AppAllVersionContainer = ({ record }) => (record.get('allAppServiceVersions') ? (
  <Select
    className={`${cssPrefix}-form-version-select`}
    style={{ width: '100%', border: 'none' }}
    clearButton={false}
    valueField="id"
    textField="version"
    onChange={(selected) => {
      record.set('selectedVersion', selected);
    }}
    value={record.get('selectedVersion')}
    primitiveValue={false}
  >
    {record.get('allAppServiceVersions').map((item) => (
      <Option value={item.id}>{item.version}</Option>
    ))}
  </Select>
) : <span>无版本</span>);

const DocumentField = observer((versionDataSet) => {
  const setDoc = (value) => {
    versionDataSet.current.set('document', value);
  };

  return (
    <div style={{ width: '5.12rem', marginTop: '-0.12rem' }}>
      <p className={`${cssPrefix}-document-title`} style={{ marginBottom: 0 }}>
        <span>文档<p style={{ display: 'inline-block', marginLeft: '0.04rem', color: '#d50000', fontSize: '0.14rem' }}>*</p></span>
      </p>
      <Editor
        prefix="document"
        onRef={() => { }}
        onChange={setDoc}
        value={versionDataSet.current ? versionDataSet.current.get('document') : ''}
      />
    </div>
  );
});

const ChangeLogField = observer((versionDataSet) => (
  <div className={`${cssPrefix}-changelog`} name="changeLog" style={{ width: 512, marginTop: '-0.12rem' }}>
    <p>ChangeLog<p style={{ display: 'inline-block', marginLeft: '0.04rem', marginBottom: 0, color: '#d50000', fontSize: '0.14rem' }}>*</p></p>
    <ChangeLogEditor current={versionDataSet.current} />
  </div>
));

const ReleaseNewVersion = observer((props) => {
  const context = useContext(Store);

  const { versionDataSet, serviceTableDataSet, selectVersionsDataSet, allServiceTableDataSet } = context;

  const apiReducer = (action) => {
    const whetherToCreate = versionDataSet.current.get('whetherToCreate');
    if (whetherToCreate && allServiceTableDataSet.selected.length === 0) {
      return Promise.reject(Error('至少选择一个应用服务'));
    }
    const updateObj = {
      ...versionDataSet.current.toJSONData(),
      latestVersionId: versionDataSet.current.get('whetherToCreate') ? undefined : serviceTableDataSet.queryDataSet.current.get('versionId'),
      createVersion: versionDataSet.current.get('whetherToCreate') ? {
        version: versionDataSet.current.get('version'),
        serviceVersionIds: allServiceTableDataSet.selected.map((item) => item.get('selectedVersion').id),
      } : undefined,
    };
    const validateFunc = () => {
      if (whetherToCreate) {
        return versionDataSet.validate();
      } else {
        return serviceTableDataSet.queryDataSet.validate();
      }
    };
    return validateFunc().then((validatePass) => {
      if (!validatePass) {
        return Promise.reject(Error('校验未通过'));
      } else {
        switch (action) {
          case 'submit':
            return axios.post(`iam/choerodon/v1/projects/${context.projectId}/publish_applications/${context.publishAppId}/new_version?apply=true&organization_id=${context.organizationId}`, updateObj);
          case 'save':
            return axios.post(`iam/choerodon/v1/projects/${context.projectId}/publish_applications/${context.publishAppId}/new_version?apply=false&organization_id=${context.organizationId}`, updateObj);
          default:
            Promise.reject(Error('非预设定 Action'));
        }
      }
    }).then((res) => {
      if (res.failed) {
        return Promise.reject(Error(res.message));
      }
      context.modal.close();
    });
  };

  const getCustomValidateMsg = (name) => {
    const currentField = context.versionDataSet.current.getField(name);
    return currentField.isValid() ? '' : currentField.getValidationMessage();
  };

  useEffect(() => {
    context.modal.update({
      footer: (okBtn, cancelBtn) => (
        <div>
          {okBtn}
          <Button
            onClick={() => apiReducer('submit')}
            funcType="raised"
            color="primary"
          >
            申请
          </Button>
          {cancelBtn}
        </div>
      ),
    });
  }, []);

  context.modal.handleOk(() => apiReducer('save'));

  const renderStatusTag = (status) => {
    if (status === 'released') {
      return <StatusTag name="已发布" color={colorMap(status)} />;
    } else if (status === 'publishing') {
      return <StatusTag name="发布中" color={colorMap(status)} />;
    } else {
      return null;
    }
  };

  const renderVersionOption = ({ record, text, value }) => (
    <span className="version-option-text">
      <span>{text}</span>
      {renderStatusTag(record.get('status'))}
    </span>
  );

  const ServiceTable = observer(() => (
    <Collapse className={`${cssPrefix}-collapse`} bordered={false} defaultActiveKey={['1']}>
      <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
        {
          versionDataSet.current && versionDataSet.current.get('whetherToCreate')
            ? (
              <Table className={`${cssPrefix}-table`} dataSet={allServiceTableDataSet} sortable={false} queryBar="none" style={{ width: '5.12rem' }}>
                <Column name="name" />
                <Column name="allAppServiceVersions" renderer={AppAllVersionContainer} minWidth={200} />
              </Table>
            )
            : (
              <Table className={`${cssPrefix}-table`} dataSet={serviceTableDataSet} sortable={false} queryBar="none" style={{ width: '5.12rem' }}>
                <Column name="name" />
                <Column name="appServiceVersions" renderer={AppVersionContainer} minWidth={200} />
              </Table>
            )
        }
      </Panel>
    </Collapse>
  ));

  const setDoc = (value) => {
    versionDataSet.current.set('document', value);
  };

  const AdditionInfo = observer(() => (
    <React.Fragment>
      <Form dataSet={versionDataSet} className={`${cssPrefix}-form`}>
        <TextField name="notificationEmail" help="该邮箱用于接收消息通知" showHelp="tooltip" />
      </Form>
      <div className={`${cssPrefix}-changelog required`}>
        <p className={`${cssPrefix}-changelog-title`}>
          <span className="required-title">ChangeLog</span>
        </p>
        <span className="required-message" style={{ display: versionDataSet.current && getCustomValidateMsg('changelog') ? 'inline-block' : 'none' }}>{versionDataSet.current && getCustomValidateMsg('changelog')}</span>
        <ChangeLogEditor current={versionDataSet.current} />
      </div>
      <div className={`${cssPrefix}-document required`}>
        <p className={`${cssPrefix}-document-title`}>
          <span className="required-title">文档</span>
        </p>
        <span className="required-message" style={{ display: versionDataSet.current && getCustomValidateMsg('document') ? 'inline-block' : 'none' }}>{versionDataSet.current && getCustomValidateMsg('document')}</span>
        <Editor
          prefix="document"
          onRef={() => { }}
          onChange={setDoc}
          value={versionDataSet.current ? versionDataSet.current.get('document') : ''}
        />
      </div>
      <Form dataSet={versionDataSet} className={`${cssPrefix}-form`}>
        <TextArea name="remark" resize="vertical" />
      </Form>
    </React.Fragment>
  ));

  const AppInfo = observer(() => (
    <Form dataSet={versionDataSet} className={`${cssPrefix}-form`}>
      <TextField name="name" disabled style={{ width: '5.12rem' }} />
      <SelectBox name="whetherToCreate" style={{ width: 512, marginTop: '0.1rem', marginBottom: '-0.1rem' }}>
        <Option value={false}>已有应用版本</Option>
        <Option value>新建应用版本</Option>
      </SelectBox>
    </Form>
  ));

  const SelectNewVersion = observer(() => (versionDataSet.current && versionDataSet.current.get('whetherToCreate')
    ? (
      <Form dataSet={versionDataSet} className={`${cssPrefix}-form`}>
        <TextField name="version" style={{ width: '5.12rem' }} />
      </Form>
    )
    : (
      <Form className={`${cssPrefix}-form`} dataSet={serviceTableDataSet.queryDataSet}>
        <Select
          name="version"
          style={{ width: '5.12rem' }}
          clearButton={false}
          onChange={() => {
            serviceTableDataSet.query();
          }}
          optionRenderer={renderVersionOption}
          onOption={({ record }) => ({
            disabled: record.get('status') === 'released' || record.get('status') === 'processing',
          })}
        />
      </Form>
    )));

  if (!versionDataSet.current) {
    return (
      <Spin />
    );
  }

  return (
    <div className={cssPrefix}>
      <AppInfo />
      <SelectNewVersion />
      <hr className={`${cssPrefix}-hr`} />
      <ServiceTable />
      <hr className={`${cssPrefix}-hr`} />
      <AdditionInfo />
    </div>
  );
});
export default ReleaseNewVersion;
