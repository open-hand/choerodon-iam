import React, { useContext, Fragment, useEffect, useState } from 'react';
import { Form, Output, Modal, TextField, Password, Progress, Button, message } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';
import { useInterval } from '../../../../../components/costomHooks';
import Store from './stores';


// 计算时间差
function handleLoadTime(startTime, endTime) {
  if (startTime && endTime) {
    const formatDate1 = startTime.replace(/ /g, '/');
    const formatData2 = endTime.replace(/ /g, '/');
    const date1 = new Date(formatDate1);
    const date2 = new Date(formatData2);
    const date3 = date2.getTime() - date1.getTime();// 时间差的毫秒数

    // 计算出相差天数
    const days = Math.floor(date3 / (24 * 3600 * 1000));
    // 计算出小时数

    const leave1 = date3 % (24 * 3600 * 1000);// 计算天数后剩余的毫秒数
    const hours = Math.floor(leave1 / (3600 * 1000));
    // 计算相差分钟数
    const leave2 = leave1 % (3600 * 1000);// 计算小时数后剩余的毫秒数
    const minutes = Math.floor(leave2 / (60 * 1000));

    // 计算相差秒数

    const leave3 = leave2 % (60 * 1000);// 计算分钟数后剩余的毫秒数
    const seconds = Math.round(leave3 / 1000);
    const setDay = days !== 0 ? `${days}天` : '';
    const setHours = hours !== 0 ? `${hours}小时` : '';
    const setMinutes = minutes !== 0 ? `${minutes} 分钟` : '';
    const setSeconds = `${seconds} 秒`;
    const time = setDay + setHours + setMinutes + setSeconds;
    return time;
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
  const { orgId, ldapId, ldapLoadClientDataSet, modal } = useContext(Store);
  const [isShowCancelBtn, changeBtnStatus] = useState(false);
  const [delay, setDelay] = useState(false);
  const newUserCount = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('newUserCount');
  const syncBeginTime = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('syncBeginTime');
  const syncEndTime = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('syncEndTime');
  const id = ldapLoadClientDataSet.current && ldapLoadClientDataSet.current.get('id');

  let intervalId;

  async function pollHistory() {
    try {
      await ldapLoadClientDataSet.query();
      if (ldapLoadClientDataSet.current.get('syncEndTime')) {
        setDelay(false);
      } else {
        setDelay(3000);
        if (new Date() - new Date(ldapLoadClientDataSet.current.get('syncBeginTime')) > 3600000) {
          changeBtnStatus(true);
        } else {
          changeBtnStatus(false);
        }
      }
    } catch (e) {
      return false;
    }
  }

  // 取消加载
  function cancelLoadClient() {
    clearInterval(intervalId);
    axios.put(`/base/v1/organizations/${orgId}/ldaps/stop`);
  }
  modal.handleOk(async () => {
    const result = await axios.post(`/base/v1/organizations/${orgId}/ldaps/sync_users`);
    if (!result.failed) {
      pollHistory();
      setDelay(3000);
    } else {
      message.error(result.message);
    }
    return false;
  });
  function renderLastSync() {
    return (
      <div style={{ fontSize: '.16rem', display: syncEndTime ? 'block' : 'none' }} className="c7n-organization-loader-header">
        <p>上次同步时间<strong>{syncBeginTime}</strong></p>
        <p>(耗时<strong>{handleLoadTime(syncBeginTime, syncEndTime)}</strong>，同步<strong>{newUserCount || 0}</strong>个用户)</p>
      </div>
    );
  }
  function renderEmpty() {
    return (
      <p><strong>暂无同步记录</strong></p>
    );
  }
  useInterval(pollHistory, delay);
  return (
    <div>
      {id ? renderLastSync() : renderEmpty()}
      <div className="c7n-organization-loader" style={{ display: !syncEndTime && id ? 'block' : 'none' }}>
        <div style={{ textAlign: 'center' }}>
          <Progress type="loading" size="large" />
          <span>正在同步中</span>
        </div>

        <p>(本次同步将会耗时较长，您可以先返回进行其他操作)</p>
        <Button
          color="red"
          funcType="raised"
          style={{
            margin: '0 auto', display: isShowCancelBtn ? 'block' : 'none',
          }}
          onClick={cancelLoadClient}
        >
          取消同步
        </Button>
      </div>
    </div>
  );
});

export default LdapLoadClient;
