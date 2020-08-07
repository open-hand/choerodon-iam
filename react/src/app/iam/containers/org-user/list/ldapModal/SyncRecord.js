import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';
import moment from 'moment';
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
        <Column className="text-gray" name="syncType" renderer={renderType} />
        <Column className="text-gray" name="updateUserCount" renderer={renderNewsNum} />
        <Column className="text-gray" name="errorUserCount" renderer={renderErrorUserCount} />
        <Column className="text-gray" name="syncEndTime" renderer={handleLoadTime} />
      </Table>
    </div>
  );
});

export default autoContent;
