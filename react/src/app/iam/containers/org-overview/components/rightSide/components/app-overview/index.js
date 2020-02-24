import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';

const AppOverview = observer(() => (
  <div className="c7n-overview-appOverview">
    <ContainerBlock width="100%" height={286}>应用服务概览</ContainerBlock>
  </div>
));

export default AppOverview;
