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
            <TimeLine />
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
