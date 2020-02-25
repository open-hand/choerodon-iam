import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';
import TimeLine from '../time-line';
import './index.less';

const optsRecord = [
  {
    id: 1,
    month: 'Feb',
    day: '23',
    title: '创建项目',
    icon: 'project_line',
    isDisabled: false,
    content: 'Leon 创建了项目【Choerodon】',
  },
  {
    id: 2,
    month: 'Feb',
    day: '22',
    title: '停用项目',
    icon: 'project_line',
    isDisabled: true,
    content: 'Leon 停用了项目【Choerodon】',
  },
  {
    id: 3,
    month: 'Feb',
    day: '21',
    title: '权限分配',
    icon: 'account_circle',
    isDisabled: false,
    content: 'Will 为 Leon分配了【组织成员】',
  },
  {
    id: 4,
    month: 'Feb',
    day: '22',
    title: '权限分配',
    icon: 'account_circle',
    isDisabled: true,
    content: '用户XXX（用户名）被 XXX（用户名)',
  },
  {
    id: 5,
    month: 'Feb',
    day: '22',
    title: '重试事务',
    icon: 'project_line',
    isDisabled: false,
    content: 'Leon 重试了事务【iam-enabl】',
  },
];


const RecentOpts = observer(() => (
  <div className="c7n-overview-recentOpts">
    <ContainerBlock width="100%">
      <span className="c7n-overview-recentOpts-header">组织层操作记录</span>
      <TimeLine dataSource={optsRecord} />
    </ContainerBlock>
  </div>
));

export default RecentOpts;
