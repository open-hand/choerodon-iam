import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';
import './index.less';

import PieChart from './PieChart';

const AppOverview = observer(() => (
  <div className="c7n-overview-appOverview">
    <ContainerBlock width="100%" height={286}>
      <div className="c7n-overview-appOverview-header">
        <span>应用服务概览</span>
        <span>单位：应用服务数量（个）</span>
      </div>
      <PieChart />
    </ContainerBlock>
  </div>
));

export default AppOverview;
