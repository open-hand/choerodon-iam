import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { Table, Form, TextArea, TextField, Tabs, Select, SelectBox, Spin } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import classnames from 'classnames';
import './EditAppDeatil.less';
import { Collapse, message } from 'choerodon-ui';
import Store from './Store';
import UploadLogo from '../../../../components/UploadLogo';
import Editor from '../../../../components/editor';

const { Column } = Table;

const { Panel } = Collapse;

const { TabPane } = Tabs;

const { Option } = Select;

const cssPrefix = 'c7n-market-editAppDetail';

const EditAppDetail = observer((props) => {
  const { viewAndEditAppDetailDataSet, modal, mobxStore, projectId, organizationId, appId, editReleased } = useContext(Store);

  const setUpload = (value) => {
    viewAndEditAppDetailDataSet.current.set('imageUrl', value);
  };

  const getCustomValidateMsg = (name) => {
    const currentField = viewAndEditAppDetailDataSet.current.getField(name);
    return currentField.isValid() ? '' : currentField.getValidationMessage();
  };

  const apiReducer = (type) => {
    switch (type) {
      case 'save':
        // 编辑应用接口
        if (editReleased) {
          viewAndEditAppDetailDataSet.submitUrl = `iam/choerodon/v1/projects/${projectId}/publish_applications/published_apps/${appId}`;
        } else {
          viewAndEditAppDetailDataSet.submitUrl = `iam/choerodon/v1/projects/${projectId}/publish_applications/unpublish_apps/${appId}`;
        }
        break;
      default:
        break;
    }
    viewAndEditAppDetailDataSet.current.set('overview', mobxStore.overview);
    return viewAndEditAppDetailDataSet.submit();
  };

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

  return (
    <Spin dataSet={viewAndEditAppDetailDataSet}>
      <div className={cssPrefix}>
        <UploadLogo setUpload={setUpload} img={viewAndEditAppDetailDataSet.current ? viewAndEditAppDetailDataSet.current.get('imageUrl') : ''} projectId={projectId} />
        <Form labelLayout="float" dataSet={viewAndEditAppDetailDataSet} style={{ width: '5.12rem' }} key="APP" className={`${cssPrefix}-form`} columns={2}>
          <TextField name="name" disabled={editReleased} colSpan={2} />
          <TextField name="contributor" disabled colSpan={2} />
          <TextField name="notificationEmail" disabled={editReleased} help="该邮箱用于接收消息通知" showHelp="tooltip" colSpan={2} />
          <Select name="categoryOption" clearButton={false} disabled={editReleased} />
          <TextField name="categoryEditName" disabled={editReleased || (viewAndEditAppDetailDataSet.current && viewAndEditAppDetailDataSet.current.get('categoryOption').type !== 'custom')} />
          <TextArea name="description" resize="vertical" colSpan={2} />
          <SelectBox name="free" disabled={editReleased} colSpan={2}>
            <Option value>是</Option>
            <Option value={false}>否</Option>
          </SelectBox>
          <SelectBox name="publishType" disabled={editReleased} colSpan={2}>
            <Option value="mkt_code_only">源代码</Option>
            <Option value="mkt_deploy_only">部署包</Option>
          </SelectBox>
        </Form>
        <div
          className={classnames(`${cssPrefix}-overview`, {
            required: editReleased,
          })}
        >
          <p className={`${cssPrefix}-overview-title`}>
            <span
              className={classnames({
                'required-title': editReleased,
              })}
            >
              应用介绍
            </span>
          </p>
          <span className="required-message" style={{ display: viewAndEditAppDetailDataSet.current && getCustomValidateMsg('overview') ? 'inline-block' : 'none' }}>{viewAndEditAppDetailDataSet.current && getCustomValidateMsg('overview')}</span>
          <Editor
            onRef={() => {}}
            onChange={(value) => mobxStore.setOverview(value)}
            value={mobxStore.overview}
          />
        </div>
      </div>
    </Spin>
  );
});
export default EditAppDetail;
