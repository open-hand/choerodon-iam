import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import {
  DataSet,
  Table,
  Form,
  TextArea,
  TextField,
  NumberField,
  Spin,
  Button,
  Tabs,
  Select,
  Output, SelectBox, Tooltip, Icon,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import classnames from 'classnames';
import './ConfirmApp.less';
import { Collapse, message } from 'choerodon-ui';
import Store from './Store';
import Editor from '../../../../components/editor';
import ChangeLogEditor from '../../../../components/ChangeLogEditor';
import ExpandMoreColumn from '../../component/expandMoreColumn';

const { Column } = Table;

const { Panel } = Collapse;

const { TabPane } = Tabs;

const { Option } = Select;

const cssPrefix = 'c7n-market-confirmApp';

const VersionContainer = ({ value }) => value.toJS().map(({ version }) => (
  <span className={`${cssPrefix}-serviceContainer`}>{version}</span>
));

const ConfirmApp = observer((props) => {
  const {
    confirmAppDataSet, serviceTableDataSet, status, modal, mobxStore,
    projectId, appId, versionId, organizationId,
  } = useContext(Store);
  const getCustomValidateMsg = (name) => {
    const currentField = confirmAppDataSet.current.getField(name);
    return currentField.isValid() ? '' : currentField.getValidationMessage();
  };

  const apiReducer = (type) => {
    switch (type) {
      case 'submit':
        confirmAppDataSet.submitUrl = `iam/choerodon/v1/projects/${projectId}/publish_applications/${appId}/versions/${versionId}/confirm?organization_id=${organizationId}&publish=true`;
        break;
      case 'save':
        confirmAppDataSet.submitUrl = `iam/choerodon/v1/projects/${projectId}/publish_applications/${appId}/versions/${versionId}/confirm?organization_id=${organizationId}&publish=false`;
        break;
      default:
        break;
    }
    confirmAppDataSet.current.set('document', mobxStore.document);
    confirmAppDataSet.current.set('overview', mobxStore.overview);
    return confirmAppDataSet.submit();
  };

  // useEffect(() => {
  //   if (confirmAppDataSet.current && confirmAppDataSet.current.get('overview')) {
  //     setOverview(confirmAppDataSet.current.get('overview'));
  //   }
  // }, [confirmAppDataSet.current]);

  const handleSubmit = () => apiReducer('submit').then((res) => {
    if (!res) {
      message.error('校验不通过');
      return Promise.reject();
    }
    if (res.failed) {
      message.error(res.message);
      return Promise.reject();
    }
    modal.close();
  });

  modal.handleOk(() => apiReducer('save').then((res) => {
    if (!res) {
      message.error('校验不通过');
      return false;
    }
    if (res.failed) {
      message.error(res.message);
      return false;
    }
    modal.close();
  }));

  useEffect(() => {
    modal.update({
      footer: (okBtn, cancelBtn) => (
        <React.Fragment>
          {okBtn}
          <Button
            onClick={() => handleSubmit()}
            funcType="raised"
            color="primary"
          >
            发布
          </Button>
          {cancelBtn}
        </React.Fragment>
      ),
    });
  }, []);

  return (
    <Spin dataSet={confirmAppDataSet}>
      <div className={cssPrefix}>
        <Form labelLayout="float" dataSet={confirmAppDataSet} style={{ width: '5.12rem' }} key="APP" className={`${cssPrefix}-form`}>
          <TextField name="name" disabled />
          <TextField name="version" disabled />
        </Form>
        <hr className={`${cssPrefix}-hr`} />
        <div className={`${cssPrefix}-service`}>
          <Collapse bordered={false} defaultActiveKey={['1']} className={`${cssPrefix}-service-collapse`}>
            <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
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
                      maxLength={300}
                      nameField="version"
                    />
                  )}
                />
              </Table>
            </Panel>
          </Collapse>
        </div>
        <hr className={`${cssPrefix}-hr`} />
        <Form labelLayout="float" dataSet={confirmAppDataSet} style={{ width: '5.12rem', marginTop: '0.2rem' }} key="APP" className={`${cssPrefix}-form`}>
          <TextField name="categoryName" disabled />
          <TextArea name="description" resize="vertical" />
        </Form>
        <div className={`${cssPrefix}-overview required`}>
          <p className={`${cssPrefix}-overview-title`}>
            <span className="required-title">应用介绍</span>
          </p>
          <span className="required-message" style={{ display: confirmAppDataSet.current && getCustomValidateMsg('overview') ? 'inline-block' : 'none' }}>{confirmAppDataSet.current && getCustomValidateMsg('overview')}</span>
          <Editor
            prefix="overview"
            onRef={() => {}}
            onChange={(value) => mobxStore.setOverview(value)}
            value={mobxStore.overview}
          />
        </div>
        <div className={`${cssPrefix}-changelog required`}>
          <p className={`${cssPrefix}-changelog-title`}>
            <span className="required-title">ChangeLog</span>
          </p>
          <span className="required-message" style={{ display: confirmAppDataSet.current && getCustomValidateMsg('changelog') ? 'inline-block' : 'none' }}>{confirmAppDataSet.current && getCustomValidateMsg('changelog')}</span>
          <ChangeLogEditor current={confirmAppDataSet.current} />
        </div>
        <div className={`${cssPrefix}-document`}>
          <p className={`${cssPrefix}-document-title required`}>
            <span className="required-title">文档</span>
          </p>
          <span className="required-message" style={{ display: confirmAppDataSet.current && getCustomValidateMsg('document') ? 'inline-block' : 'none' }}>{confirmAppDataSet.current && getCustomValidateMsg('document')}</span>
          <Editor
            prefix="document"
            onRef={() => {}}
            onChange={(value) => mobxStore.setDocument(value)}
            value={mobxStore.document}
          />
        </div>
      </div>
    </Spin>
  );
});
export default ConfirmApp;
