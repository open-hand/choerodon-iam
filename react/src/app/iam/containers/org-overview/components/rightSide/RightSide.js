import React from 'react';
import { observer } from 'mobx-react-lite';
import AppOverview from './components/app-overview';
import RecentOpts from './components/recent-opts';

import './RightSide.less';


const RightSide = observer(() => (
  <div className="c7n-overview-rightSide">
    <AppOverview />
    <RecentOpts />
  </div>
));

export default RightSide;
