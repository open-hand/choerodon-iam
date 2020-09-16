import React, { useContext, Fragment } from 'react';
import {
  axios, Content, Header, Page, Permission, Breadcrumb, TabPage,
} from '@choerodon/boot';
import {
  Form, Output, Modal, message,
} from 'choerodon-ui/pro';
import { SketchPicker } from 'react-color';
import { withRouter } from 'react-router-dom';
import { Button, Modal as OldModal } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import './index.less';

import InfoForm from './InfoForm';

import Store from '../stores';

const modalKey = Modal.key();

const InfoView = observer(() => {
  const {
    systemSettingDataSet: dataSet, AppState, intl, intlPrefix, presetColors, colorMap, hasRegister,
  } = useContext(Store);
  const favicon = dataSet.current && dataSet.current.getPristineValue('favicon');
  const systemLogo = dataSet.current && dataSet.current.getPristineValue('systemLogo');
  const themeColor = dataSet.current && dataSet.current.getPristineValue('themeColor');
  function handleRefresh() {
    dataSet.query();
  }
  async function handleSave() {
    try {
      if ((await dataSet.submit())) {
        // setTimeout(() => { window.location.reload(true); }, 1000);
        return true;
      }
      return false;
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
        <InfoForm intl={intl} dataSet={dataSet} AppState={AppState} hasRegister={hasRegister} />
      ),
      fullScreen: true,
      onOk: handleSave,
      okText: '保存',
      onCancel: handleCancel,
    });
  }
  function handleColorChange({ hex }) {
    const secondColor = colorMap[hex] || hex;
    dataSet.current.set('themeColor', `${hex},${secondColor}`);
  }
  function openThemeColorModal() {
    Modal.open({
      key: modalKey,
      drawer: true,
      title: '修改主题色',
      style: { width: 380 },
      children: (
        <SketchPicker
          width="inherit"
          color={(themeColor && themeColor.split(',')[0]) || '#3f51b5'}
          onChangeComplete={handleColorChange}
          presetColors={presetColors}
        />
      ),
      fullScreen: true,
      onOk: handleSave,
      okText: '保存',
      onCancel: handleCancel,
    });
  }
  function handleReset() {
    OldModal.confirm({
      className: 'c7n-iam-confirm-modal',
      title: intl.formatMessage({ id: `${intlPrefix}.reset.confirm.title` }),
      content: intl.formatMessage({ id: `${intlPrefix}.reset.confirm.content` }),
      onOk: async () => {
        try {
          await axios.delete('/iam/choerodon/v1/system/setting');
          await window.location.reload(true);
          return true;
        } catch (e) {
          return false;
        }
      },
    });
  }
  function renderThemeColor({ value }) {
    return (
      <div style={{
        width: '.2rem', height: '.2rem', background: ((value && value.split(',')[0]) || '#3F51B5'), marginTop: '.05rem', borderRadius: '.02rem',
      }}
      />
    );
  }

  function renderBoolean({ value }) {
    return value === true ? '是' : '否';
  }

  return (
    <TabPage service={['choerodon.code.site.setting.general-setting.ps.default']}>
      <Header>
        <Permission service={['choerodon.code.site.setting.general-setting.ps.update']}>
          <Button type="primary" funcType="flat" icon="mode_edit" onClick={openModal}> 修改信息 </Button>
        </Permission>
        <Permission service={['choerodon.code.site.setting.general-setting.ps.update.theme']}>
          <Button type="primary" funcType="flat" icon="mode_edit" onClick={openThemeColorModal}> 修改主题色 </Button>
        </Permission>
        <Permission service={['choerodon.code.site.setting.general-setting.ps.reset']}>
          <Button type="primary" funcType="flat" icon="swap_horiz" onClick={handleReset}> 重置 </Button>
        </Permission>
      </Header>

      <Breadcrumb />

      <Content className="c7n-system-setting-page-content">
        <div className="c7n-system-setting-form">
          <h3>平台信息</h3>
          <div className="divider" />
          <Form
            pristine
            labelWidth={190}
            dataSet={dataSet}
            labelLayout="horizontal"
            labelAlign="left"
            columns={2}
          >
            <Output name="systemName" colSpan={1} />

            <div colSpan={1} rowSpan={3} className="c7n-system-setting-formImg" label="平台LOGO">
              {favicon ? <img src={favicon} alt="图片" />
                : <div className="c7n-system-setting-formImg-wrapper default-favicon" />}
            </div>
            <Output name="systemTitle" newLine />
            <Output renderer={() => '简体中文'} name="defaultLanguage" newLine />
            <Output renderer={() => (dataSet.current && dataSet.current.getPristineValue('resetGitlabPasswordUrl')) || '无'} name="resetGitlabPasswordUrl" />
            <div colSpan={1} rowSpan={3} className="c7n-system-setting-formImg" label="平台导航栏图形标">
              {systemLogo ? <img src={systemLogo} alt="图片" />
                : <div className="c7n-system-setting-formImg-wrapper default-logo" />}
            </div>
            {hasRegister && (
              <Output renderer={renderBoolean} name="registerEnabled" newLine />
            )}
            {hasRegister && dataSet.current && dataSet.current.getPristineValue('registerEnabled') && (
              <Output renderer={() => (dataSet.current && dataSet.current.getPristineValue('registerUrl')) || '无'} name="registerUrl" />
            )}
            <Output renderer={renderBoolean} name="autoCleanEmailRecord" newLine label="是否自动清理邮件日志" />
            <Output renderer={renderBoolean} name="autoCleanWebhookRecord" newLine label="是否自动清理webhook日志" />
            <Output renderer={renderBoolean} name="autoCleanSagaInstance" newLine label="是否自动清理事务记录" />
          </Form>
        </div>

        <div className="c7n-system-setting-form">
          <h3>主题色</h3>
          <div className="divider" />
          <Form
            pristine
            labelWidth={180}
            dataSet={dataSet}
            labelLayout="horizontal"
            labelAlign="left"
          >
            <Output renderer={renderThemeColor} name="themeColor" />
          </Form>
        </div>
      </Content>
    </TabPage>
  );
});
export default withRouter(InfoView);
