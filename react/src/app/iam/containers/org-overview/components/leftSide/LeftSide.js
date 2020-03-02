import React from 'react';
import { observer } from 'mobx-react-lite';
import ProColony from './components/pro-colony';
import OrgPeople from './components/org-people';
import ProDeploy from './components/pro-deploy';
import ThingPerform from './components/thing-perform';

import './LeftSide.less';

const LeftSide = observer(() => (
  <div className="c7n-overview-leftSide">
    <ProColony />
    <OrgPeople />
    <ProDeploy />
    <ThingPerform />
  </div>
));

export default LeftSide;
