import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';

import './index.less';

const ProColony = observer(() => (
  <div className="c7n-overview-procolony">
    <ContainerBlock width="49%" height={140}>项目</ContainerBlock>
    <ContainerBlock width="49%" height={140}>集群</ContainerBlock>
  </div>
));

export default ProColony;
