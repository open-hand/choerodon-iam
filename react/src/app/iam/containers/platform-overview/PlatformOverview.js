import React from 'react';
import { observer } from 'mobx-react-lite';
import { Breadcrumb, TabPage, Content } from '@choerodon/boot';

import ContainerBlock from '../org-overview/components/ContainerBlock';

import './PlatformOverview.less';

const PlatformOverview = observer(() => (
  <TabPage className="c7n-platform-overview">
    <Breadcrumb />
    <Content>
      <div className="c7n-platform-overview-firstline">
        <ContainerBlock
          width="40%"
          height="100%"
          title="在线人数统计"
        />
        <ContainerBlock
          width="57%"
          height="100%"
          title="平台人数统计"
        />
      </div>
      <div className="c7n-platform-overview-content">
        <div className="c7n-platform-overview-content-left">
          <ContainerBlock
            width="100%"
            height="400px"
            title="邮件发送情况"
          />
          <ContainerBlock
            style={{
              marginTop: 20,
            }}
            width="100%"
            height="574px"
            title="事物执行情况"
          />
        </div>
        <div className="c7n-platform-overview-content-right">
          <ContainerBlock
            width="100%"
            height="528px"
            title="系统公告"
          />
          <ContainerBlock
            style={{
              marginTop: 20,
            }}
            width="100%"
            height="678px"
            title="平台层操作记录"
          />
        </div>
      </div>
    </Content>
  </TabPage>
));

export default PlatformOverview;
