import React, { useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import moment from 'moment';
import ContainerBlock from '../../../org-overview/components/ContainerBlock';
import { usePlatformPeopleStore } from './stores';
import Chart from './Chart';

const PlatformPeople = observer(() => {
  const [chosenDay, setChosenDay] = useState(7);

  const {
    PlatformPeopleStore,
  } = usePlatformPeopleStore();

  const initData = (day) => {
    const startTime = moment().subtract(day, 'days').format('YYYY-MM-DD HH:mm:ss');
    const endTime = moment().format('YYYY-MM-DD HH:mm:ss');
    PlatformPeopleStore.initPlatformPeopleChartData(startTime, endTime);
  };

  useEffect(() => {
    initData(chosenDay);
  }, []);

  const handleChangeDays = (days) => {
    setChosenDay(days);
    initData(days);
  };

  return (
    <ContainerBlock
      width="58%"
      height="100%"
      title="平台人数统计"
      hasDaysPicker
      handleChangeDays={handleChangeDays}
    >
      <Chart />
    </ContainerBlock>
  );
});

export default PlatformPeople;
