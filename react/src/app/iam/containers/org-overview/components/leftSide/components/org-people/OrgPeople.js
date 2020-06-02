import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import ContainerBlock from '../../../ContainerBlock';
import { useOrgPeopleStore } from './stores';
import Charts from './Charts';

import './index.less';

const OrgPeople = observer(() => {
  const [chosenDay, setChosenDay] = useState(7);

  const {
    OrgPeopleStore,
    AppState: {
      menuType: { orgId },
    },
  } = useOrgPeopleStore();

  const initData = (day) => {
    const startTime = moment().subtract(day, 'days').format('YYYY-MM-DD HH:mm:ss');
    const endTime = moment().format('YYYY-MM-DD HH:mm:ss');
    OrgPeopleStore.initOrgPeopleDataByDate(orgId, startTime, endTime);
  };

  useEffect(() => {
    initData(chosenDay);
  }, []);

  const handleChangeDays = (days) => {
    setChosenDay(days);
    initData(days);
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
