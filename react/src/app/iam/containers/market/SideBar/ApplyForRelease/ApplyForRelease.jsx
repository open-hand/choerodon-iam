/* eslint-disable */
import React, { Component, useState, useContext, useEffect, useReducer } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Table, TextField, NumberField, Modal, Button, Tabs, SelectBox } from 'choerodon-ui/pro';
import { Steps, message } from 'choerodon-ui';
import { axios } from '@choerodon/boot'
import { FirstStep, SecondStep, ThirdStep } from './StepForm';
import Store from './Store';
import './ApplyForRelease.less';

const cssPrefix = 'c7n-market-applyForRelease';

const steps = [{
  title: '选择应用',
  content: <FirstStep />,
}, {
  title: '申请发布',
  content: <SecondStep />,
}, {
  title: '确认信息',
  content: <ThirdStep />,
}];

const { Step } = Steps;

const stepReducer = (count, action) => {
  switch (action) {
    case 'next':
      // eslint-disable-next-line no-return-assign
      return count += 1;
    case 'previous':
      // eslint-disable-next-line no-return-assign
      return count -= 1;
    default: throw new Error();
  }
};

const ApplyForRelease = observer((props) => {
  const { modal, projectId, organizationId, newPlatformAppTableDataSet, versionNameDataSet, versionOptionDataSet, existPlatformAppTableDataSet, marketAppDataSet, mobxStore } = useContext(Store);
  const [step, stepDispatch] = useReducer(stepReducer, 0);

  const convertPublishType = () => {
    if (marketAppDataSet.current.get('publishType').includes('mkt_code_only', 'mkt_deploy_only') && marketAppDataSet.current.get('publishType').length === 2) {
      return 'mkt_code_deploy';
    }
    return marketAppDataSet.current.get('publishType')[0];
  };

  const convertCategoryName = () => {
    if (marketAppDataSet.current.get('categoryOption').type === 'custom') {
      return {
        categoryName: marketAppDataSet.current.get('categoryName'),
      };
    }
    return {
      categoryCode: marketAppDataSet.current.get('categoryOption').code,
      categoryName: marketAppDataSet.current.get('categoryOption').name,
    };
  };

  const apiReducer = (action) => {
    // const  = use(Store);
    const updateObj = {
      ...marketAppDataSet.toJSONData()[0],
      refAppId: mobxStore.createType === 'exist' ? existPlatformAppTableDataSet.queryDataSet.current.get('applicationId') : newPlatformAppTableDataSet.queryDataSet.current.get('applicationId'),
      publishType: convertPublishType(),
      ...convertCategoryName(),
      whetherToCreate: mobxStore.createType === 'new',
      latestVersionId: mobxStore.createType === 'exist' ? mobxStore.latestVersionId : undefined,
      createVersion: mobxStore.createType === 'new' ? ({
        version: versionNameDataSet.current.get('versionName'),
        serviceVersionIds: newPlatformAppTableDataSet.selected.map((item) => item.get('appServiceVersions').id),
      }) : undefined,
    };
    switch (action) {
      case 'save':
        return axios.post(`base/v1/projects/${projectId}/publish_applications`, updateObj, {
          params: {
            apply: false,
            organization_id: organizationId,
          }
        }).then((res) => {
          if (res.failed) {
            message.error(res.message);
          } else {
            message.success('保存成功')
          }
          modal.close();
        });
        break;
      case 'submit':
        return axios.post(`base/v1/projects/${projectId}/publish_applications`, updateObj, {
          params: {
            apply: true,
            organization_id: organizationId,
          }
        }).then((res) => {
          if (res.failed) {
            message.error(res.message);
          } else {
            message.success('申请发布成功')
          }
          modal.close();
        });
        break;
      default:
        break;
    }
  };

  const btnGroupMap = (step, stepDispatch, cancelBtn) => {
    switch (step) {
      case 0:
        return (
          <React.Fragment>
            <Button
              onClick={async () => {
                try {
                  const optionValidate = await versionOptionDataSet.queryDataSet.validate();
                  if (!optionValidate) {
                    throw new Error();
                  }
                  if (mobxStore.createType === 'new' && newPlatformAppTableDataSet.selected.length === 0) {
                    return message.error('至少选择一个应用服务');
                    throw new Error();
                  }
                  if (mobxStore.createType === 'new') {
                    const versionNameValidate = await versionNameDataSet.validate();
                    if (!versionNameValidate) {
                      message.error('校验未通过');
                      throw new Error();
                    } else {
                      stepDispatch('next');
                    }
                  } else {
                    const versionIdValidate = await existPlatformAppTableDataSet.queryDataSet.validate();
                    if (!versionIdValidate) {
                      throw new Error();
                    } else {
                      mobxStore.setLatestVersionId(existPlatformAppTableDataSet.queryDataSet.current.get('versionId'));
                      stepDispatch('next');
                    }
                  }
                } catch (e) {
                  return false;
                }
              }}
              funcType="raised"
              color="primary"
            >
              下一步
            </Button>
            {cancelBtn}
          </React.Fragment>
        );
      case 1:
        return (
          <React.Fragment>
            <Button
              funcType="raised"
              color="primary"
              onClick={() => {
                return marketAppDataSet.validate().then((validatePass) => {
                  if (validatePass) {
                    return apiReducer('save');
                  } else {
                    message.error('校验未通过');
                    return false;
                  }
                })
              }}
            >
              保存
            </Button>
            <Button
              onClick={() => {
                marketAppDataSet.validate().then((validatePass) => {
                  if (validatePass) {
                    stepDispatch('next');
                  } else {
                    message.error('校验未通过');
                  }
                })
              }}
              funcType="raised"
              color="primary"
            >
              下一步
            </Button>
            <Button
              onClick={() => stepDispatch('previous')}
              funcType="raised"
            >
              上一步
            </Button>
            {cancelBtn}
          </React.Fragment>
        );
      case 2:
        return (
          <React.Fragment>
            <Button
              funcType="raised"
              color="primary"
              onClick={() => apiReducer('save')}
            >
              保存
            </Button>
            <Button funcType="raised" color="primary" onClick={() => apiReducer('submit')}>保存并申请</Button>
            <Button onClick={() => stepDispatch('previous')} funcType="raised">上一步</Button>
            {cancelBtn}
          </React.Fragment>
        );
      default:
        break;
    }
  };
  
  useEffect(() => {
    modal.update({
      footer: (okBtn, cancelBtn) => btnGroupMap(step, stepDispatch, cancelBtn),
    });
  }, [step]);
  return (
    <div className={cssPrefix}>
      <Steps current={step}>
        {steps.map(item => <Step key={item.title} title={item.title} />)}
      </Steps>
      <div className="steps-content">{steps[step].content}</div>
    </div>
  );
});
export default ApplyForRelease;
