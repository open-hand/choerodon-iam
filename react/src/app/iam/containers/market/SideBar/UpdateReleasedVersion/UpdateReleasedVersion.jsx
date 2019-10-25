import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { DataSet, Table, Form, Output, TextField, TextArea, Modal, Button, CheckBox, Tooltip, Icon } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Collapse, message, Badge } from 'choerodon-ui';
import { Link } from 'react-router-dom';
import Store from './Store';
import Editor from '../../../../components/editor';
import ChangeLogEditor from '../../../../components/ChangeLogEditor';
import './UpdateReleasedVersion.less';
import ExpandMoreColumn from '../../component/expandMoreColumn';

const { Column } = Table;
const { Panel } = Collapse;

const cssPrefix = 'c7n-market-updateVersionDetail';

const versionStatusMap = {
  failure: 'error',
  done: 'success',
  // processing 有动画，会导致显示不全
  processing: 'default',
  unpublished: 'warning',
};

const ViewVersionDetail = observer((props) => {
  const { updateReleasedVersionDataSet, serviceTableDataSet, mobxStore, modal, status, projectId, organizationId, history } = useContext(Store);
  const getCustomValidateMsg = (name) => {
    const currentField = updateReleasedVersionDataSet.current.getField(name);
    return currentField.isValid() ? '' : currentField.getValidationMessage();
  };

  const apiReducer = (type) => {
    updateReleasedVersionDataSet.current.set('document', mobxStore.document);
    return updateReleasedVersionDataSet.submit();
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

  useEffect(() => {
    modal.update({
      footer: (okBtn, cancelBtn) => (
        <React.Fragment>
          {okBtn}
          {cancelBtn}
        </React.Fragment>
      ),
    });
  }, []);

  // IMPORTANT: Editor 组件运用 id 进行元素定位，需要加入 prefix 确保 id 的唯一性

  return (
    <div className={cssPrefix}>
      <Form labelLayout="float" dataSet={updateReleasedVersionDataSet} style={{ width: '5.12rem' }} key="APP" className={`${cssPrefix}-form`}>
        <TextField name="version" disabled />
        <CheckBox name="whetherToFix" disabled={updateReleasedVersionDataSet.current && updateReleasedVersionDataSet.current.get('disableWhetherToFix')} />
      </Form>
      <hr className={`${cssPrefix}-hr`} />
      <Collapse bordered={false} defaultActiveKey={['1']} className={`${cssPrefix}-service-collapse`}>
        <Panel header={(<span style={{ marginLeft: '0.1rem' }}>包含的应用服务</span>)} key="1">
          <p className={`${cssPrefix}-serviceInfo`}>
            <Icon type="info" className={`${cssPrefix}-service-serviceInfo-icon`} />
            <span>如果您想修改应用服务及应用服务版本，请前去<Link to={`/base/application-management${history.location.search}`}>应用管理</Link>进行编辑，修改后的数据将会在此同步更新</span>
          </p>
          <Table dataSet={serviceTableDataSet} queryBar="none" className={`${cssPrefix}-serviceTable`}>
            <Column name="name" width={150} />
            <Column
              name="appServiceVersions"
              width={362}
              header="应用服务版本"
              className={`${cssPrefix}-serviceContainer`}
              renderer={(renderProps) => (
                <ExpandMoreColumn
                  {...renderProps}
                  maxLength={362}
                  nameField="version"
                  showBadge={(service, index, fromPopOver) => (index > 0 || fromPopOver) && service.status !== 'done'}
                  badgeConfig={(service, index) => ({
                    status: versionStatusMap[service.status],
                    offset: [6, 0],
                  })}
                />
              )}
            />
          </Table>
        </Panel>
      </Collapse>
      <hr className={`${cssPrefix}-hr`} />
      <div className={`${cssPrefix}-changelog required`}>
        <p className={`${cssPrefix}-changelog-title`}>
          <span className="required-title">ChangeLog</span>
        </p>
        <span className="required-message" style={{ display: updateReleasedVersionDataSet.current && getCustomValidateMsg('changelog') ? 'inline-block' : 'none' }}>{updateReleasedVersionDataSet.current && getCustomValidateMsg('changelog')}</span>
        <ChangeLogEditor current={updateReleasedVersionDataSet.current} />
      </div>
      <div className={`${cssPrefix}-document`}>
        <p className={`${cssPrefix}-document-title required`}>
          <span className="required-title">文档</span>
        </p>
        <span className="required-message" style={{ display: updateReleasedVersionDataSet.current && getCustomValidateMsg('document') ? 'inline-block' : 'none' }}>{updateReleasedVersionDataSet.current && getCustomValidateMsg('document')}</span>
        <Editor
          prefix="document"
          onRef={() => {}}
          onChange={(value) => mobxStore.setDocument(value)}
          value={mobxStore.document}
        />
      </div>
    </div>
  );
});
export default ViewVersionDetail;
