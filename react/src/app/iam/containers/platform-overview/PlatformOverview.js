import React from 'react';
import { observer } from 'mobx-react-lite';
import { Breadcrumb, TabPage, Content } from '@choerodon/boot';

import ContainerBlock from '../org-overview/components/ContainerBlock';
import PlatformPeople from './component/platform-people';
import ThingPerform from './component/thing-perform';
import EmailSend from './component/email-send';

import './PlatformOverview.less';
import TimeLine from './component/time-line';
import LineChart from './component/onLine-chart';
import OptsLine from './component/opts-line';
// 公告模拟数据
const notifications = [
  {
    id: 1,
    month: 'Feb',
    day: '23',
    title: 'Choerodon平台将于2019/12/20升级',
    icon: 'notifications_none',
    content: 'Choerodon平台将于2019/12/20(星期五) 21:00-24:00进行升级维护，届时可能无法登录系统页面，gitlab等组件不会影响使用，请提前做好准备，系统升级对你造成的不便，敬请谅解。',
  },
  {
    id: 2,
    month: 'Feb',
    day: '22',
    icon: 'notifications_none',
    title: 'Choerodon平台将于2019/12/20升级',
    content: 'Choerodon平台将于2019/12/20(星期五) 我1:00-24:00进行升级维护，届时可能无法登录系统页面，gitlab等组件不会影响使用，请提前做好准备，系统升级对你造成的不便，敬请谅解。',
  },
  {
    id: 3,
    month: 'Feb',
    day: '21',
    icon: 'notifications_none',
    title: 'Choerodon平台将于2019/12/20升级',
    content: 'Choerodon平台将于2019/12/20(星期五)',
  },
];

// 操作记录模拟数据
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

const PlatformOverview = observer(() => (
  <TabPage className="c7n-platform-overview">
    <Breadcrumb />
    <Content style={{ paddingTop: 0 }}>
      <div className="c7n-platform-overview-firstline">
        <ContainerBlock
          width="40%"
          height="100%"
          title="在线人数统计"
        >
          <LineChart />
        </ContainerBlock>
        <PlatformPeople />
      </div>
      <div className="c7n-platform-overview-content">
        <div className="c7n-platform-overview-content-left">
          <EmailSend />
          <ThingPerform />
        </div>
        <div className="c7n-platform-overview-content-right">
          <ContainerBlock
            width="100%"
            // height="528px"
            title="系统公告"
          >
            <TimeLine dataSource={notifications} />
          </ContainerBlock>
          <ContainerBlock
            style={{
              marginTop: 20,
            }}
            width="100%"
            title="平台层操作记录"
          >
            <OptsLine />
          </ContainerBlock>
        </div>
      </div>
    </Content>
  </TabPage>
));

export default PlatformOverview;
