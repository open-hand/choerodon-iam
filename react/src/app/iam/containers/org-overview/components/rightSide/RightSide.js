import React from 'react';
import { observer } from 'mobx-react-lite';
import AppOverview from './components/app-overview';

import './RightSide.less';

const RightSide = observer(() => (
  <div className="c7n-overview-rightSide">
    <AppOverview />
  </div>
));

export default RightSide;
