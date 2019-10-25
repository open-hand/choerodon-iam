/* eslint-disable */
import React, { Component, useEffect, useState } from 'react';
import { Upload, Icon, message } from 'choerodon-ui';
import { Choerodon } from '@choerodon/boot';
import { injectIntl, FormattedMessage, IntlProvider } from 'react-intl';
import './UploadLogo.scss';

const limitSize = 1024;

const intlPrefix = 'global';

const lessThan2M = (file) => file.size / 1024 / 1024 < 2;

const cssPrefix = 'market-logo-uploader';

export default class UploadLogo extends Component {
  state = {
    loading: false,
  };

  uploadProps = (setUpload) => {
    const { projectId } = this.props;
    return ({
      multiple: false,
      name: 'file',
      listType: 'picture-card',
      className: cssPrefix,
      accept: 'image/jpeg, image/png, image/jpg',
      action: `${Choerodon.API_HOST}/base/v1/projects/${projectId}/publish_applications/cut_image`,
      headers: {
        Authorization: `bearer ${Choerodon.getCookie('access_token')}`,
      },
      showUploadList: false,
      beforeUpload: (file) => {
        if (!lessThan2M(file)) {
          // Choerodon.prompt(intl.formatMessage({ id: `${intlPrefix}.file.size.limit` }, { size: `${limitSize / 1024}M` }));
          Choerodon.prompt('文件超过1M');
          return false;
        }
        // this.setState({ file });
        // const windowURL = window.URL || window.webkitURL;
        // if (windowURL && windowURL.createObjectURL) {
        //   this.loadImage(windowURL.createObjectURL(file));
        //   return false;
        // }
      },
      onChange: ({ file: { status, response } }) => {
        if (status === 'done') {
          // this.setState({ imageUrl: response });
          setUpload(response);
        } else if (status === 'error') {
          Choerodon.prompt('错误');
        }
      },
    });
  }

  render() {
    const { img, setUpload, validationMessage } = this.props;
    const uploadButton = (
      <div className={`${cssPrefix}-img`}>
        <Icon type={this.state.loading ? 'loading' : 'photo_camera'} />
      </div>
    );
    return (
      <div className={`${cssPrefix}-imgContainer`} style={{ width: 512 }}>
        <Upload
          {...this.uploadProps(setUpload)}
        >
          {img ? (
            <div className="dimback">
              <Icon type="photo_camera" />
              <img src={img} alt="" style={{ width: '100%', height: '100%', borderRadius: '50%', position: 'absolute', top: 0, left: 0 }} />
            </div>
          ) : uploadButton}
        </Upload>
        <p><FormattedMessage id={`${intlPrefix}.imageUploader`} /><span style={{ color: '#d50000' }}>  *</span></p>
        <p><span style={{ color: '#d50000', textAlign: 'center', marginTop: 0 }}>{validationMessage}</span></p>
      </div>
    );
  }
}
