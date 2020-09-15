import React, { useState, useCallback } from 'react';
import { Icon, Button } from 'choerodon-ui';

const TimeItem = ((props) => {
  const {
    renderDateLine,
    sendDate,
    title,
    content,
  } = props;

  const [isExpand, handleDropDown] = useState(false);

  const renderContent = useCallback(() => ({ __html: content }), [content]);

  return (
    <li>
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
            icon="expand_more"
            type="primary"
            size="small"
            onClick={() => handleDropDown(!isExpand)}
            style={{ minWidth: '0.24rem' }}
          />
        </div>
        <div
          dangerouslySetInnerHTML={renderContent()}
          className="c7n-pFlow"
          style={{ display: isExpand ? 'block' : 'none' }}
        />
        <div
          dangerouslySetInnerHTML={renderContent()}
          className="c7n-pFlow-overflow"
          style={{ display: !isExpand ? 'block' : 'none' }}
        />
      </div>
    </li>
  );
});

export default TimeItem;
