/* eslint-disable jsx-a11y/click-events-have-key-events, jsx-a11y/no-static-element-interactions */

import React, { useEffect, useState } from 'react';
import {
  Form, TextField, TextArea, Select, SelectBox, NumberField,
} from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Button, Icon, Tooltip } from 'choerodon-ui';
import './index.less';
import AvatarUploader from '../../../components/avatarUploader';
import Tips from '../../../components/new-tips';

const { Option } = Select;
const InfoForm = observer(({
  dataSet, AppState, intl, hasRegister,
}) => {
  const favicon = dataSet.current && dataSet.current.get('favicon');
  const systemLogo = dataSet.current && dataSet.current.get('systemLogo');
  const [isShowAvatar, changeAvatarStatus] = useState(false);
  const [avatarType, setAvatarType] = useState();
  function openModalUpload(type) {
    changeAvatarStatus(true);
    setAvatarType(type);
  }
  // 关闭模态框
  function closeAvatarUploader() {
    changeAvatarStatus(false);
  }
  function getTitle() {
    return avatarType === 'favicon' ? '上传平台Logo' : '上传导航栏图形标';
  }
  async function handleUploadOk(url) {
    dataSet.current.set(avatarType, url);
    closeAvatarUploader();
  }
  function renderAvatar() {
    return (
      <div className="c7n-system-setting-avater-container">
        <div style={{ transform: 'translate(0.51rem, 0)', width: '.8rem' }}>
          <div className="c7n-system-setting-avater">
            <div colSpan={1} rowSpan={2} className="c7n-system-setting-formImg" label="平台Logo：">
              {!favicon && <div className="c7n-system-setting-formImg-wrapper default-favicon" />}
              {favicon && <div style={{ backgroundImage: `url(${favicon})` }} className="c7n-system-setting-formImg-wrapper" />}
            </div>
            <Button>
              <div className="c7n-iam-system-setting-avatar-button-icon" onClick={() => openModalUpload('favicon')}>
                <Icon type="photo_camera" />
              </div>
            </Button>
          </div>
          <span style={{
            display: 'block', textAlign: 'center', fontSize: '.13rem', color: 'rgba(0,0,0,0.54)', marginTop: '.06rem',
          }}
          >
            平台Logo
          </span>
        </div>
        <div style={{ transform: 'translate(1.91rem, -1rem)', width: '1.1rem' }}>
          <div className="c7n-system-setting-avater">
            <div colSpan={1} rowSpan={2} className="c7n-system-setting-formImg" label="平台导航栏图形标：">
              {!systemLogo && <div className="c7n-system-setting-formImg-wrapper default-logo" />}
              {systemLogo && <div style={{ backgroundImage: `url(${systemLogo})` }} className="c7n-system-setting-formImg-wrapper" />}
            </div>
            <Button>
              <div className="c7n-iam-system-setting-avatar-button-icon" onClick={() => openModalUpload('systemLogo')}>
                <Icon type="photo_camera" />
              </div>
            </Button>
          </div>
          <span style={{
            display: 'block', textAlign: 'center', fontSize: '.13rem', color: 'rgba(0,0,0,0.54)', marginTop: '.06rem',
          }}
          >
            平台导航栏图形标
          </span>
        </div>
      </div>
    );
  }
  return (
    <div className="c7n-system-setting-infoForm">
      <Form dataSet={dataSet} labelLayout="float">
        {renderAvatar()}
        <TextField name="systemName" required />
        <TextField name="systemTitle" />
        <Select label="默认语言" defaultValue="zh_CN" required>
          <Option value="zh_CN">简体中文</Option>
        </Select>
        <TextArea resize="vertical" name="resetGitlabPasswordUrl" required />
        {hasRegister && (
          <SelectBox name="registerEnabled">
            <SelectBox.Option value>是</SelectBox.Option>
            <SelectBox.Option value={false}>否</SelectBox.Option>
          </SelectBox>
        )}
        {hasRegister && dataSet.current && dataSet.current.get('registerEnabled') && (
          <TextArea resize="vertical" name="registerUrl" />
        )}
        <Tips
          title="是否自动清理邮件日志"
          helpText="若选择自动清理平台中邮件日志后，系统将在每天凌晨2点自动清理超出“保留时间”的邮件日志"
          className="c7n-system-setting-infoForm-label"
        />
        <SelectBox name="autoCleanEmailRecord">
          <SelectBox.Option value>是</SelectBox.Option>
          <SelectBox.Option value={false}>否</SelectBox.Option>
        </SelectBox>
        {dataSet.current && dataSet.current.get('autoCleanEmailRecord') && (
          <NumberField
            name="autoCleanEmailRecordInterval"
            suffix={<span className="c7n-system-setting-infoForm-suffix">天</span>}
            addonAfter={<Tips helpText="若将日志保留时间设置为180天，即表示每天会自动清除180天之前的日志" />}
          />
        )}
        <Tips
          title="是否自动清理Webhook日志"
          helpText="若选择自动清理平台中Webhook日志后，系统将在每天凌晨2点自动清理超出“保留时间”的Webhook日志"
          className="c7n-system-setting-infoForm-label"
        />
        <SelectBox name="autoCleanWebhookRecord">
          <SelectBox.Option value>是</SelectBox.Option>
          <SelectBox.Option value={false}>否</SelectBox.Option>
        </SelectBox>
        {dataSet.current && dataSet.current.get('autoCleanWebhookRecord') && (
          <NumberField
            name="autoCleanWebhookRecordInterval"
            suffix={<span className="c7n-system-setting-infoForm-suffix">天</span>}
            addonAfter={<Tips helpText="若将日志保留时间设置为180天，即表示每天会自动清除180天之前的日志" />}
          />
        )}
      </Form>

      <AvatarUploader
        title={getTitle()}
        visible={isShowAvatar}
        AppState={AppState}
        intl={intl}
        intlPrefix="global.organization.avatar.edit"
        onVisibleChange={closeAvatarUploader}
        onUploadOk={(url) => handleUploadOk(url)}
      />
    </div>
  );
});

export default InfoForm;
