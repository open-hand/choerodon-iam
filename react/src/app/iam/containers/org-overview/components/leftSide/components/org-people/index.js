import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';

import './index.less';

const OrgPeople = observer(() => (
  <div className="c7n-overview-orgPeople">
    <ContainerBlock width="100%" height={306}>组织人数</ContainerBlock>
  </div>
));

export default OrgPeople;
