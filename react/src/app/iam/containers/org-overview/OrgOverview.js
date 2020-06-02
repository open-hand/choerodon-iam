import React from 'react';
import { observer } from 'mobx-react-lite';
import { Breadcrumb, TabPage, Content } from '@choerodon/boot';
import LeftSide from './components/leftSide';
import RightSide from './components/rightSide';

import './OrgOverview.less';

const OrgOverview = observer(() => (
  <TabPage className="c7n-org-overview" service={['choerodon.code.organization.manager.overview.ps.default']}>
    <Breadcrumb />
    <Content className="c7n-org-overview-content" style={{ paddingTop: 0 }}>
      <LeftSide />
      <RightSide />
    </Content>
  </TabPage>
));

export default OrgOverview;
