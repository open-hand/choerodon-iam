import React, { useContext, Fragment } from 'react';
import { Action, Content, Header, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Form, Output, Modal } from 'choerodon-ui/pro';
import { withRouter } from 'react-router-dom';
import { Button } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './OrganizationBasic.less';

import InfoForm from './InfoForm';

import Store from '../stores';

const modalKey = Modal.key();

const InfoView = observer(() => {
  const { organizationDataSet: dataSet, AppState, intl, orgName } = useContext(Store);
  const imageUrl = dataSet.current && dataSet.current.getPristineValue('imageUrl');
  function handleRefresh() {
    dataSet.query();
  }
  async function handleSave() {
    try {
      if ((await dataSet.submit())) {
        handleRefresh();
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  function handleCancel() {
    dataSet.reset();
    return true;
  }
  function openModal() {
    Modal.open({
      key: modalKey,
      drawer: true,
      title: '修改信息',
      style: { width: 380 },
      children: (
        <InfoForm intl={intl} dataSet={dataSet} AppState={AppState} orgName={orgName} />
      ),
      fullScreen: true,
      onOk: handleSave,
      okText: '保存',
      onCancel: handleCancel,
    });
  }
  return (
    <TabPage
      service={['choerodon.code.organization.setting.general-setting.ps.info']}
    >

      <Header>
        <Permission service={['choerodon.code.organization.setting.general-setting.ps.update.info']}>
          <Button
            type="primary"
            funcType="flat"
            icon="mode_edit"
            onClick={openModal}
          >
            修改
          </Button>
        </Permission>
      </Header>

      <Breadcrumb />

      <Content className="c7n-organization-page-content">
        <Form
          pristine
          labelWidth={130}
          dataSet={dataSet}
          className="c7n-organization-form"
          labelLayout="horizontal"
          labelAlign="left"
          columns={3}
        >
          <Output name="tenantName" colSpan={1} />

          <div colSpan={1} rowSpan={3} className="c7n-organization-formImg" label="组织LOGO">
            {imageUrl ? <img src={imageUrl} alt="图片" />
              : <div className="c7n-organization-formImg-wrapper">{orgName[0]}</div>}

          </div>
          <Output name="tenantNum" newLine />
          <Output name="address" newLine renderer={({ text }) => (text || '无')} />
          <Output
            name="homePage"
            newLine
            renderer={({ text }) => (text || '暂未设置官网地址')}
          />
          <Output name="ownerRealName" newLine />
        </Form>
      </Content>
    </TabPage>
  );
});
export default withRouter(InfoView);
