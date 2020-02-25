import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';
import Charts from './Charts';
import FailedStatistics from './FailedStatistics';

import './index.less';

const ThingPerform = observer(() => {
  const handleChangeDays = (days) => {
    window.console.log(days);
  };
  return (
    <div className="c7n-overview-thingPerform">
      <ContainerBlock
        width="100%"
        title="事务执行情况"
        hasDaysPicker
        handleChangeDays={handleChangeDays}
      >
        <Charts />
        <FailedStatistics />
      </ContainerBlock>
    </div>
  );
});

export default ThingPerform;
