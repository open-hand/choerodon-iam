/* eslint-disable react/no-danger */
import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';

import { Icon, Button, Tooltip } from 'choerodon-ui';
import './index.less';
import { usePlatformOverviewStore } from '../../stores';

// 点击展示
function handleDropDown(e) {
  const pNode = e.currentTarget.parentNode.parentNode.getElementsByTagName('p')[0]; // p元素
  const i = e.currentTarget.getElementsByClassName('icon')[0]; // btn的图标
  if (i.classList.contains('icon-expand_more')) {
    i.classList.remove('icon-expand_more');
    i.classList.add('icon-expand_less');
    pNode.style.setProperty('white-space', 'normal');
  } else {
    i.classList.remove('icon-expand_less');
    i.classList.add('icon-expand_more');
    pNode.style.setProperty('white-space', 'nowrap');
  }
}

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
    } else {
      return false;
    }
  }
  /* eslint-disable no-shadow */
  /* eslint-disable wrap-iife */

  useEffect(() => {
    const flow = document.getElementsByClassName('c7n-pFlow');
    if (flow && flow.length > 0) {
      for (let i = 0; i < flow.length; i += 1) {
        new ResizeObserver((entries) => {
          entries.forEach((entry) => {
            const dom = entry.target;
            const pDom = dom.getElementsByTagName('p')[0];
            const scrollW = Math.ceil(pDom.scrollWidth);
            const width = Math.ceil(pDom.clientWidth);
            if (scrollW > width) {
              noticeDs.records[i].set('display', 'block');
            }
          });
        }).observe(flow[i]);
      }
    }
  });

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
            const { id, sendDate, content, title } = item;
            return (
              <li key={id}>
                {renderDateLine(sendDate)}
                <div className="c7ncd-notice-timeLine-content">
                  <div className="c7ncd-notice-timeLine-content-header">
                    <div className="c7ncd-notice-timeLine-content-header-icon">
                      <Icon type="notifications_none" />
                    </div>
                    <span className="c7ncd-notice-timeLine-content-header-title">{title}</span>

                    <Button
                      className="c7ncd-notice-timeLine-content-header-btn"
                      shape="circle"
                      funcType="flat"
                      style={{ display: item.display }}
                      icon="expand_more"
                      type="primary"
                      size="small"
                      onClick={handleDropDown}
                    />
                  </div>
                  <div dangerouslySetInnerHTML={{ __html: content }} className="c7n-pFlow" />
                </div>
              </li>
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
