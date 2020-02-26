import React from 'react';
import ContainerBlock from '../../../org-overview/components/ContainerBlock';
import Chart from './Chart';

const PlatformPeople = () => (
  <ContainerBlock
    width="57%"
    height="100%"
    title="平台人数统计"
    hasDaysPicker
  >
    <Chart />
  </ContainerBlock>
);

export default PlatformPeople;
