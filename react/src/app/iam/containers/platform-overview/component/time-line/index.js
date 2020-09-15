/* eslint-disable react/no-danger */
import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';

import { Button, Tooltip } from 'choerodon-ui';
import './index.less';
import { usePlatformOverviewStore } from '../../stores';
import TimeItem from './TimeItem';

function renderMonth(month) {
  switch (month) {
    case '01':
      month = 'Jan';
      break;
    case '02':
      month = 'Feb';
      break;
    case '03':
      month = 'Mar';
      break;
    case '04':
      month = 'Apr';
      break;
    case '05':
      month = 'May';
      break;
    case '06':
      month = 'Jun';
      break;
    case '07':
      month = 'Jul';
      break;
    case '08':
      month = 'Aug';
      break;
    case '09':
      month = 'Sept';
      break;
    case '10':
      month = 'Oct';
      break;
    case '11':
      month = 'Nov';
      break;
    default:
      month = 'Dec';
      break;
  }
  return month;
}

const TimeLine = observer(() => {
  const {
    noticeDs,
    platOverStores,
  } = usePlatformOverviewStore();

  const [isMore, setLoadMoreBtn] = useState(false);

  const record = noticeDs.current && noticeDs.toData();

  // 加载记录
  async function loadData(page = 1) {
    const res = await noticeDs.query(page);
    const records = platOverStores.getOldNoticeRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        noticeDs.unshift(...records);
      }
      platOverStores.setOldNoticeRecord(noticeDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    }
    return false;
  }

  // 更多公告
  function loadMoreNoticeRecord() {
    loadData(noticeDs.currentPage + 1);
  }

  useEffect(() => {
    loadData();
  }, []);

  function renderDateLine(date) {
    const dateArr = date && date.split('-');
    const month = renderMonth(dateArr[1]);
    return (
      <Tooltip title={date}>
        <div className="c7ncd-notice-timeLine-date">
          <span>{dateArr[2].split(' ')[0]}</span>
          <span>{month}</span>
        </div>
      </Tooltip>
    );
  }

  function renderData() {
    return record ? (
      <ul>
        {
          record.map((item) => {
            const {
              id,
            } = item;
            return (
              <TimeItem
                {...item}
                key={id}
                renderDateLine={renderDateLine}
              />
            );
          })
        }
      </ul>
    ) : null;
  }

  return (
    <div className="c7ncd-notice-timeLine">
      {record && record.length > 0 ? (
        <div className="c7ncd-notice-timeLine-body">
          {renderData()}
        </div>
      ) : <span className="c7ncd-notice-timeLine-empty">暂无更多记录...</span>}
      {isMore && <Button type="primary" onClick={loadMoreNoticeRecord}>加载更多</Button>}
    </div>
  );
});

export default TimeLine;
