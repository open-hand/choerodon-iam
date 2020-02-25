import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';
import Charts from './Charts';

import './index.less';

const OrgPeople = observer(() => {
  const handleChangeDays = (days) => {
    window.console.log(days);
  };
  return (
    <div className="c7n-overview-orgPeople">
      <ContainerBlock
        width="100%"
        height={306}
        title="组织人数统计"
        hasDaysPicker
        handleChangeDays={handleChangeDays}
      >
        <Charts />
      </ContainerBlock>
    </div>
  );
});

export default OrgPeople;
