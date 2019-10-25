import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { DataSet, Table, Form, Output, TextField, TextArea, Spin, Button, Tabs, Tooltip, Icon } from 'choerodon-ui/pro';
import { Link } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import classnames from 'classnames';
import ReactMarkdown from 'react-markdown';
import { Collapse, message } from 'choerodon-ui';
import Store from './Store';
import UploadLogo from '../../../../components/UploadLogo';
import Editor from '../../../../components/editor';
import ChangeLogEditor from '../../../../components/ChangeLogEditor';
import './EditVersionDetail.less';
import ExpandMoreColumn from '../../component/expandMoreColumn';

const { Column } = Table;
const { Panel } = Collapse;

const cssPrefix = 'c7n-market-editVersionDetail';

const ViewVersionDetail = observer((props) => {
  const { viewAndEditVersionDetailDataSet, serviceTableDataSet, mobxStore, modal, status, projectId, organizationId, history } = useContext(Store);
  const getCustomValidateMsg = (name) => {
    const currentField = viewAndEditVersionDetailDataSet.current.getField(name);
    return currentField.isValid() ? '' : currentField.getValidationMessage();
  };
  const setUpload = (value) => {
    viewAndEditVersionDetailDataSet.current.set('imageUrl', value);
  };

  const apiReducer = (type) => {
    switch (type) {
      case 'submit':
        viewAndEditVersionDetailDataSet.submitUrl = `base/v1/projects/${projectId}/publish_version_infos?organization_id=${organizationId}&apply=true`;
        break;
      case 'save':
        viewAndEditVersionDetailDataSet.submitUrl = `base/v1/projects/${projectId}/publish_version_infos?organization_id=${organizationId}&apply=false`;
        break;
      default:
        break;
    }
    viewAndEditVersionDetailDataSet.current.set('document', mobxStore.document);
    viewAndEditVersionDetailDataSet.current.set('overview', mobxStore.overview);
    return viewAndEditVersionDetailDataSet.submit();
  };

  const handleSubmit = () => apiReducer('submit').then((res) => {
    if (!res) {
      message.error('校验未通过');
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
      message.error('校验未通过');
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
            onClick={handleSubmit}
            funcType="raised"
            color="primary"
          >
            {status === 'unpublished' ? '申请' : '重新申请'}
          </Button>
          {cancelBtn}
        </React.Fragment>
      ),
    });
  }, []);

  // IMPORTANT: Editor 组件运用 id 进行元素定位，需要加入 prefix 确保 id 的唯一性

  return (
    <Spin dataSet={viewAndEditVersionDetailDataSet}>
      <div className={cssPrefix}>
        <Form labelLayout="float" dataSet={viewAndEditVersionDetailDataSet} style={{ width: '5.12rem' }} className={`${cssPrefix}-form`}>
          <UploadLogo setUpload={setUpload} img={viewAndEditVersionDetailDataSet.current ? viewAndEditVersionDetailDataSet.current.get('imageUrl') : ''} projectId={projectId} />
          <TextField name="version" disabled />
        </Form>
        <hr className={`${cssPrefix}-hr`} />
        <Collapse bordered={false} defaultActiveKey={['1']} className={`${cssPrefix}-service-collapse`}>
          <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
            <p className={`${cssPrefix}-serviceInfo`}>
              <Icon type="info" className={`${cssPrefix}-service-serviceInfo-icon`} />
              <span>如果您想修改应用服务及应用服务版本，请前去<Link to={`/base/application-management${history.location.search}`}>应用管理</Link>进行编辑，修改后的数据将会在此同步更新
              </span>
            </p>
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
        <hr className={`${cssPrefix}-hr`} />
        <Form labelLayout="float" dataSet={viewAndEditVersionDetailDataSet} style={{ width: '5.12rem', marginTop: '0.2rem' }} className={`${cssPrefix}-form`}>
          <TextArea name="description" resize="vertical" />
        </Form>
        <div className={`${cssPrefix}-overview`}>
          <p className={`${cssPrefix}-overview-title`}>
            <span>应用介绍</span>
          </p>
          <Editor
            prefix="overview"
            onRef={() => {}}
            onChange={(value) => mobxStore.setOverview(value)}
            value={mobxStore.overview}
          />
        </div>
        <div className={`${cssPrefix}-changelog`}>
          <p className={`${cssPrefix}-changelog-title`}>
            <span>ChangeLog</span>
          </p>
          <ChangeLogEditor current={viewAndEditVersionDetailDataSet.current} />
        </div>
        <div className={`${cssPrefix}-document`}>
          <p className={`${cssPrefix}-document-title`}>
            <span>文档</span>
          </p>
          <Editor
            prefix="document"
            onRef={() => {}}
            onChange={(value) => mobxStore.setDocument(value)}
            value={mobxStore.document}
          />
        </div>
        <Form labelLayout="float" dataSet={viewAndEditVersionDetailDataSet} style={{ width: '5.12rem', marginTop: '0.2rem' }} className={`${cssPrefix}-form`}>
          <TextArea name="remark" resize="vertical" />
        </Form>
      </div>
    </Spin>
  );
});
export default ViewVersionDetail;
