import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';
import { useLdapStore } from './stores';

const { Column } = Table;

const autoContent = observer(() => {
  const {
    prefixCls,
    recordTableDs,
  } = useLdapStore();

  function renderNewsNum({ record }) {
    let num = record.get('updateUserCount');
    let num2 = record.get('errorUserCount');
    let num3 = record.get('newUserCount');
    if (num == null || num2 == null) {
      num = 0;
      num2 = 0;
      num3 = 0;
    }
    return <span>{`${num + num3}/${num + num2 + num3}`}</span>;
  }
  function renderErrorUserCount({ record }) {
    let num = record.get('errorUserCount');
    if (num == null) {
      num = 0;
    }
    return <span>{num}</span>;
  }
  // 计算时间差
  function handleLoadTime({ record }) {
    const startTime = record.get('syncBeginTime');
    const endTime = record.get('syncEndTime');
    const formatDate1 = startTime.replace(/ /g, '/');
    const formatData2 = endTime.replace(/ /g, '/');
    const date1 = new Date(formatDate1);// 开始时间
    const date2 = new Date(formatData2);// 结束时间
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
  }

  function renderType({ value }) {
    if (value === 'auto') {
      return '自动同步';
    }
    return '手动同步';
  }
  
  return (
    <div className={`${prefixCls}-record-content`}>
      <Table dataSet={recordTableDs}>
        <Column className="text-gray" name="syncBeginTime" width={200} />
        <Column className="text-gray" name="type" renderer={renderType} />
        <Column className="text-gray" name="updateUserCount" renderer={renderNewsNum} />
        <Column className="text-gray" name="errorUserCount" renderer={renderErrorUserCount} />
        <Column className="text-gray" name="syncEndTime" renderer={handleLoadTime} />
      </Table>
    </div>
  );
});

export default autoContent;
