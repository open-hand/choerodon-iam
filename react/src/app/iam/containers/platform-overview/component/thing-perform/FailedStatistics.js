import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';

import { useFailedStatisticsStore } from './stores';

const { Column } = Table;

const FailedStatistics = observer(() => {
  const {
    FailedStatisticsTableDataSet,
  } = useFailedStatisticsStore();

  return (
    <div className="c7n-overview-failedStatistics">
      <span className="c7n-overview-failedStatistics-titlespan">失败事物统计</span>
      <Table
        className="c7n-overview-failedStatistics-table"
        dataSet={FailedStatisticsTableDataSet}
        queryBar="none"
      >
        <Column name="sagaCode" />
        <Column name="refType" />
        <Column name="startTime" />
      </Table>
    </div>
  );
});

export default FailedStatistics;
