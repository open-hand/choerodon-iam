import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';
import TimeLine from '../time-line';
import './index.less';


const RecentOpts = observer(() => (
  <div className="c7n-overview-recentOpts">
    <ContainerBlock width="100%">
      <span className="c7n-overview-recentOpts-header">组织层操作记录</span>
      <TimeLine />
    </ContainerBlock>
  </div>
));

export default RecentOpts;
