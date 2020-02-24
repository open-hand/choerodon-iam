import React from 'react';
import { observer } from 'mobx-react-lite';
import ContainerBlock from '../../../ContainerBlock';

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
        title="组织人数"
        hasDaysPicker
        handleChangeDays={handleChangeDays}
      />
    </div>
  );
});

export default OrgPeople;
