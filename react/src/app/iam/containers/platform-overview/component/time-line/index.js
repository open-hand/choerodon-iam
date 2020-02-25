import React from 'react';
import { observer } from 'mobx-react-lite';
import './index.less';
import { Icon, Button } from 'choerodon-ui';

// 点击加载更多
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


const TimeLine = observer(({ dataSource }) => {
  function renderData() {
    return dataSource ? (
      <ul>
        {
          dataSource.map((item, index) => {
            const { id, day, month, content, title, icon, isDisabled } = item;
            return (
              <li key={id}>
                <div className="c7ncd-timeLine-date">
                  <span>{day}</span>
                  <span>{month}</span>
                </div>
                <div className="c7ncd-timeLine-content">
                  <div className="c7ncd-timeLine-content-header">
                    <div className="c7ncd-timeLine-content-header-icon">
                      <Icon type={icon} className={isDisabled ? 'stop' : null} />
                    </div>
                    <span className="c7ncd-timeLine-content-header-title">{title}</span>
                    {
                      content.length > 60 ? (
                        <Button
                          className="c7ncd-timeLine-content-header-btn"
                          shape="circle"
                          funcType="flat"
                          icon="expand_more"
                          type="primary"
                          size="small"
                          onClick={handleDropDown}
                        />
                      ) : null
                    }
                  </div>
                  <p>{content}</p>
                </div>
              </li>
            );
          })
        }
      </ul>
    ) : null;
  }

  return (
    <div className="c7ncd-timeLine">
      <div className="c7ncd-timeLine-body">
        {renderData()}
      </div>
      <Button type="primary">加载更多</Button>
    </div>
  );
});

export default TimeLine;
