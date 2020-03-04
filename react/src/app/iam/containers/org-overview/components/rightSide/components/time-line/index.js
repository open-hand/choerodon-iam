import React, { useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import './index.less';
import { Icon, Button, Tooltip } from 'choerodon-ui';
import { useOrgOverviewRightSide } from '../../stores';

// 点击展示更多
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

const iconType = {
  addAdminUsers: {
    icon: 'account_circle',
    className: '',
    typeTxt: '分配root权限',
  },
  assignUsersRoles: {
    icon: 'account_circle',
    className: '',
    typeTxt: '分配角色',
  },
  createOrg: {
    icon: 'project_line',
    className: '',
    typeTxt: '创建组织',
  },
  unlockUser: {
    icon: 'account_circle',
    className: '',
    typeTxt: '解锁用户',
  },
  enableUser: {
    icon: 'account_circle',
    className: '',
    typeTxt: '启用用户',
  },
  disableUser: {
    icon: 'account_circle',
    className: 'disabled',
    typeTxt: '禁用用户',
  },
  deleteOrgAdministrator: {
    icon: 'account_circle',
    className: 'delete',
    typeTxt: '删除组织管理员角色',
  },
  createOrgAdministrator: {
    icon: 'account_circle',
    className: '',
    typeTxt: '添加组织管理员角色',
  },
  createProject: {
    icon: 'project_line',
    className: '',
    typeTxt: '创建项目',
  },
  enableProject: {
    icon: 'project_line',
    className: '',
    typeTxt: '启用项目',
  },
  disableProject: {
    icon: 'project_line',
    className: 'disabled',
    typeTxt: '禁用项目',
  },
  createUserOrg: {
    icon: 'account_circle',
    className: '',
    typeTxt: '创建用户',
  },
};

const TimeLine = observer(() => {
  const {
    optsDs,
    overStores,
  } = useOrgOverviewRightSide();

  const [isMore, setLoadMoreBtn] = useState(false);

  const record = optsDs.current && optsDs.toData();

  // 加载记录
  async function loadData(page = 1) {
    const res = await optsDs.query(page);
    const records = overStores.getOldOptsRecord;
    if (res && !res.failed) {
      if (!res.isFirstPage) {
        optsDs.unshift(...records);
      }
      overStores.setOldOptsRecord(optsDs.records);
      setLoadMoreBtn(res.hasNextPage);
      return res;
    } else {
      return false;
    }
  }
  // 更多操作
  function loadMoreOptsRecord() {
    loadData(optsDs.currentPage + 1);
  }

  useEffect(() => {
    loadData();
  }, []);

  function renderDateLine(date) {
    const dateArr = date && date.split('-');
    const month = renderMonth(dateArr[1]);
    return (
      <Tooltip title={date}>
        <div className="c7ncd-timeLine-date">
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
            const { id, creationDate, type, content } = item;
            return (
              <li key={id}>
                {renderDateLine(creationDate)}
                <div className="c7ncd-timeLine-content">
                  <div className="c7ncd-timeLine-content-header">
                    <div className="c7ncd-timeLine-content-header-icon">
                      <Icon type={iconType[type].icon} className={iconType[type].className} />
                    </div>
                    <span className="c7ncd-timeLine-content-header-title">{iconType[type].typeTxt}</span>
                    {
                      content.length > 50 ? (
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
                <div className="c7ncd-timeLine-border">
                  <div />
                  <div />
                  <div />
                  <div />
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
      {
        record && record.length > 0 ? (
          <div className="c7ncd-timeLine-body">
            {renderData()}
          </div>
        ) : '暂无更多记录...'
      }
      {isMore && <Button type="primary" onClick={loadMoreOptsRecord}>加载更多</Button>}
    </div>
  );
});

export default TimeLine;
