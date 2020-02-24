import React from 'react';
import { observer } from 'mobx-react-lite';
import { Breadcrumb, TabPage, Content } from '@choerodon/boot';
import LeftSide from './components/leftSide';
import RightSide from './components/rightSide';

import './OrgOverview.less';

const OrgOverview = observer(() => (
  <TabPage className="c7n-org-overview">
    <Breadcrumb />
    <Content className="c7n-org-overview-content">
      <LeftSide />
      <RightSide />
    </Content>
  </TabPage>
));

export default OrgOverview;
