import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';
import { Button, Upload } from 'choerodon-ui';
import Store from './stores';
import { useInterval } from '../../../../components/costomHooks';
import './index.less';

export default observer(() => {
  const { prefixCls, intlPrefix, intl, organizationId, userId } = useContext(Store);

  // 导入用户角色部分
  const [syncData, setSyncData] = useState({});
  const [uploading, setUploading] = useState(false);
  const [delay, setDelay] = useState(false);
  
  async function pollHistory() {
    const timestamp = new Date().getTime();
    const result = await axios.get(`/base/v1/organizations/${organizationId}/member_role/users/${userId}/upload/history?t=${timestamp}`);
    setSyncData(result);
    if (result.id && !result.endTime) {
      setDelay(3000);
    } else {
      setDelay(false);
    }
    return result;
  }
  /**
   *  application/vnd.ms-excel 2003-2007
   *  application/vnd.openxmlformats-officedocument.spreadsheetml.sheet 2010
   */
  function getUploadProps() {
    return {
      multiple: false,
      name: 'file',
      accept: 'application/vnd.ms-excel, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      // eslint-disable-next-line no-underscore-dangle
      action: organizationId && `${window._env_.API_HOST || process.env.API_HOST}/base/v1/organizations/${organizationId}/role_members/batch_import`,
      headers: {
        Authorization: `bearer ${Choerodon.getCookie('access_token')}`,
      },
      showUploadList: false,
      onChange: ({ file }) => {
        const { status, response } = file;
        if (status === 'done') {
          if (!response.failed) {
            Choerodon.prompt('上传成功');
            pollHistory();
            setDelay(3000);
          } else {
            Choerodon.prompt(response.message);
          }
          setUploading(false);
        } else if (status === 'error') {
          setUploading(false);
          Choerodon.prompt(`${response.message}`);
        } else if (status === 'uploading') {
          setUploading(true);
        }
      },
    };
  }

  function getSpentTime() {
    const { startTime, endTime } = syncData;
    const timeUnit = {
      day: intl.formatMessage({ id: 'day' }),
      hour: intl.formatMessage({ id: 'hour' }),
      minute: intl.formatMessage({ id: 'minute' }),
      second: intl.formatMessage({ id: 'second' }),
    };
    const spentTime = new Date(endTime).getTime() - new Date(startTime).getTime(); // 时间差的毫秒数
    // 天数
    const days = Math.floor(spentTime / (24 * 3600 * 1000));
    // 小时
    const leave1 = spentTime % (24 * 3600 * 1000); //  计算天数后剩余的毫秒数
    const hours = Math.floor(leave1 / (3600 * 1000));
    // 分钟
    const leave2 = leave1 % (3600 * 1000); //  计算小时数后剩余的毫秒数
    const minutes = Math.floor(leave2 / (60 * 1000));
    // 秒数
    const leave3 = leave2 % (60 * 1000); //  计算分钟数后剩余的毫秒数
    const seconds = Math.round(leave3 / 1000);
    const resultDays = days ? (days + timeUnit.day) : '';
    const resultHours = hours ? (hours + timeUnit.hour) : '';
    const resultMinutes = minutes ? (minutes + timeUnit.minute) : '';
    const resultSeconds = seconds ? (seconds + timeUnit.second) : '';
    const totolTime = resultDays + resultHours + resultMinutes + resultSeconds;
    return totolTime || '0';
  }

  async function handleDownLoad() {
    axios.get(`/base/v1/organizations/${organizationId}/role_members/download_templates`, {
      responseType: 'arraybuffer',
    }).then((result) => {
      const blob = new Blob([result], {
        type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8' });
      const url = window.URL.createObjectURL(blob);
      const linkElement = document.getElementById('c7n-user-download-template');
      linkElement.setAttribute('href', url);
      linkElement.click();
    });
  }

  function getLoading() {
    let info;
    if (uploading) {
      info = '上传中...';
    }
    info = '同步中...';
    return (
      <p>{info}</p>
    );
  }

  function getInfo() {
    return (
      <div>
        <p className="last-import-time">上次导入完成时间<span className="import-user-time">{syncData.endTime}</span>{syncData.endTime && `（耗时${getSpentTime()}秒）`}</p>
        <p className="total-import">共导入
          <span className="import-user-success">
            {syncData.successfulCount || 0}
          </span>条数据成功,
          <span className="import-user-failed">
            {syncData.failedCount || 0}
          </span>条数据失败
        </p>
        <a className="download-detail" href={syncData.url || null} target="_blank" rel="noopener noreferrer">
          点击下载失败详情
        </a>
      </div>
    );
  }

  function renderUploadPanel() {
    if ((syncData.id && !syncData.endTime) || uploading) {
      return getLoading();
    } else {
      return getInfo();
    }
  }
  
  useInterval(pollHistory, delay);

  function renderImportUserRole() {
    return (
      <React.Fragment>
      
        <h3>下载模板</h3>
        <p>您必须使用模版文件，录入组织用户信息</p>
        
        <Button onClick={handleDownLoad} type="primary" funcType="flat" icon="get_app">
          下载模板
          <a label=" " style={{ display: 'none' }} id="c7n-user-download-template" href="a" onClick={(event) => { event.stopPropagation(); }} download="roleAssignment.xlsx" />
        </Button>
        <div className="divider-space top-30" />
        <div className="divider" />
        <h3 className="import-user-title">导入组织用户</h3>
        <span>{renderUploadPanel()}</span>
        <Upload {...getUploadProps(organizationId)}>
          <Button disabled={uploading || (syncData.id && !syncData.endTime)} type="primary" funcType="flat" icon="file_upload">上传文件</Button>
        </Upload>
      </React.Fragment>
    );
  }

  // END 导入用户角色部分

  return (
    <div
      className={`import-user ${prefixCls}-modal`}
    >
      {renderImportUserRole()}
    </div>
  );
});
