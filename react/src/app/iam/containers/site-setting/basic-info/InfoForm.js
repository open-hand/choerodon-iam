import React, { useEffect, useState } from 'react';
import { Form, TextField, TextArea, Select, SelectBox, UrlField, DatePicker } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Button, Icon, Input } from 'choerodon-ui';
import './index.less';
import AvatarUploader from '../../../components/avatarUploader';

const { Option } = Select;
const InfoForm = observer(({ dataSet, AppState, intl }) => {
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
          <span style={{ display: 'block', textAlign: 'center', fontSize: '.13rem', color: 'rgba(0,0,0,0.54)', marginTop: '.06rem' }}>平台Logo</span>
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
          <span style={{ display: 'block', textAlign: 'center', fontSize: '.13rem', color: 'rgba(0,0,0,0.54)', marginTop: '.06rem' }}>平台导航栏图形标</span>
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
