import React, { useEffect, useState } from 'react';
import { Form, TextField, TextArea, Select, Output } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { Button, Icon, Input } from 'choerodon-ui';
import './OrganizationBasic.less';
import AvatarUploader from '../../../../components/avatarUploader';


const InfoForm = observer(({ dataSet, AppState, intl, orgName }) => {
  const imageUrlDefault = dataSet.current && dataSet.current.get('imageUrl');
  const [imageUrl, changeUrl] = useState(imageUrlDefault);
  const [isShowAvatar, changeAvatarStatus] = useState(false);
  function openModalUpload() {
    changeAvatarStatus(true);
  }
  // 关闭模态框
  function closeAvatarUploader() {
    changeAvatarStatus(false);
  }
  async function handleUploadOk(url) {
    // console.log(dataSet);
    dataSet.current.set('imageUrl', url);
    changeUrl(`${url}`);
    try {
      if ((await dataSet.submit()) !== false) {
        dataSet.query();
        changeAvatarStatus(false);
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }
  return (
    <div className="c7n-organization-infoForm">
      <Form dataSet={dataSet} labelLayout="float">
        <div className="c7n-organization-avater-container">
          <div
            className="c7n-organization-avater"
            style={{
              backgroundImage: imageUrl ? `url(${imageUrl})` : '',
            }}
          >
            {!imageUrl && (
            <div style={{ position: 'absolute' }} colSpan={1} rowSpan={2} className="c7n-organization-formImg" label="组织LOGO">
              <div className="c7n-organization-formImg-wrapper">{orgName[0]}</div>
            </div>
            )}
            <Button>
              <div className="c7n-iam-organizationsetting-avatar-button-icon" onClick={openModalUpload}>
                <Icon type="photo_camera" />
              </div>
            </Button>
            <AvatarUploader
              visible={isShowAvatar}
              AppState={AppState}
              intl={intl}
              intlPrefix="global.organization.avatar.edit"
              onVisibleChange={closeAvatarUploader}
              onUploadOk={handleUploadOk}
            />
          </div>
          <span style={{ display: 'block', textAlign: 'center', fontSize: '.13rem', color: 'rgba(0,0,0,0.54)' }}>组织Logo</span>
        </div>
        <TextField label="组织名称*" name="name" required />
        <TextField label="组织编码" name="code" disabled />
        <TextField label="组织所在地*" name="address" required />
        <TextField label="官网地址*" name="homePage" required />
        <TextField label="所有者" name="ownerRealName" disabled />
      </Form>
    </div>
  );
});

export default InfoForm;
