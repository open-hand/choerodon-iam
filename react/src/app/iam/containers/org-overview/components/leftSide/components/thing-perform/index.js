import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';

import './index.less';

const ThingPerform = observer(() => (
  <div className="c7n-overview-thingPerform">
    <ContainerBlock width="100%" height={574}>事物执行</ContainerBlock>
  </div>
));

export default ThingPerform;
