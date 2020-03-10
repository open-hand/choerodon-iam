import React, { useContext, useState } from 'react';
import { Progress, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import moment from 'moment';
import { useInterval } from '../../../../../components/costomHooks';
import Store from './stores';

import './index.less';

// 计算时间差
function handleLoadTime(startTime, endTime) {
  if (startTime && endTime) {
    const releaseDate = moment(endTime);
    const currentDate = moment(startTime);

    const diff = releaseDate.diff(currentDate);
    const diffDuration = moment.duration(diff);

    const diffYears = diffDuration.years();
    const diffMonths = diffDuration.months();
    const diffDays = diffDuration.days();
    const diffHours = diffDuration.hours();
    const diffMinutes = diffDuration.minutes();
    const diffSeconds = diffDuration.seconds();

    return `${diffYears ? `${diffYears}年` : ''}${diffMonths ? `${diffMonths}月` : ''}${diffDays ? `${diffDays}日` : ''}${diffHours ? `${diffHours}小时` : ''}${diffMinutes ? `${diffMinutes}分钟` : ''}${diffSeconds ? `${diffSeconds}秒` : ''}`;
  } else {
    return '正在计算时间...';
  }
}

/**
 * 从一个单行的DataSet中获取某个字段值
 *
 * @param {DataSet} dataSet 数据集对象
 * @param {string} fieldName 字段名
 */
export function extractFieldValueFromDataSet(dataSet, fieldName) {
  return dataSet && dataSet.current && dataSet.current.get(fieldName);
}

const LdapLoadClient = observer(() => {
  const { orgId, ldapLoadClientDataSet, modal } = useContext(Store);
  const [delay, setDelay] = useState(false);
  const newUserCount = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('newUserCount');
  const syncBeginTime = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('syncBeginTime');
  const syncEndTime = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('syncEndTime');
  const updateUserCount = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('updateUserCount');
  const errorUserCount = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('errorUserCount');
  const id = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('id');
  const [isStop, changeIsStop] = useState(false);

  let intervalId;

  async function pollHistory() {
    try {
      await ldapLoadClientDataSet.query();
      if (ldapLoadClientDataSet.current.get('syncEndTime')) {
        setDelay(false);
        changeIsStop(false);
        modal.update({ okProps: { color: 'primary', loading: false }, okText: '同步' });
      } else {
        setDelay(3000);
        if (new Date() - new Date(ldapLoadClientDataSet.current.get('syncBeginTime')) > 3600000) {
          changeIsStop(true);
          modal.update({ okProps: { color: 'red' }, okText: '终止同步' });
        } else {
          changeIsStop(false);
          modal.update({ okProps: { color: 'primary', loading: true }, okText: '同步中' });
        }
      }
    } catch (e) {
      return false;
    }
  }

  modal.handleOk(async () => {
    if (!isStop) {
      const result = await axios.post(`/base/v1/organizations/${orgId}/ldaps/sync_users`);
      if (!result.failed) {
        pollHistory();
        setDelay(3000);
      } else {
        message.error(result.message);
      }
      return false;
    } else {
      clearInterval(intervalId);
      axios.put(`/base/v1/organizations/${orgId}/ldaps/stop`);
      return false;
    }
  });

  function renderLastSync() {
    return (
      <div className="base-org-ldap-record" style={{ display: syncEndTime ? 'block' : 'none' }}>
        <div className="base-org-ldap-record-text">
          上次同步完成时间
          <span className="base-org-ldap-record-time">{syncEndTime}</span>
          &nbsp; (耗时{handleLoadTime(syncBeginTime, syncEndTime)})
        </div>
        <div>
          共同步
          <span className="base-org-ldap-record-number-success">{newUserCount + updateUserCount || 0}</span>
          个用户成功，
          <span className="base-org-ldap-record-number-failed">{errorUserCount || 0}</span>
          个用户失败
        </div>
      </div>
    );
  }
  function renderEmpty() {
    return (
      <span className="base-org-ldap-record">暂无同步记录</span>
    );
  }

  useInterval(pollHistory, delay);

  return (
    <div>
      {id ? renderLastSync() : renderEmpty()}
      <div className="base-org-ldap-sync" style={{ display: !syncEndTime && id ? 'block' : 'none' }}>
        <Progress type="loading" size="large" />
        <div className="base-org-ldap-sync-loading">正在导入中</div>
        <div>(本次导入耗时较长，您可先返回进行其他操作)</div>
      </div>
    </div>
  );
});

export default LdapLoadClient;
