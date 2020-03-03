import React from 'react';
import { observer } from 'mobx-react-lite';
import { Table } from 'choerodon-ui/pro';
import { withRouter } from 'react-router-dom';

import { useFailedStatisticsStore } from './stores';

const { Column } = Table;

const FailedStatistics = withRouter(observer((props) => {
  const {
    history,
    location: {
      search,
    },
  } = props;

  const {
    FailedStatisticsTableDataSet,
  } = useFailedStatisticsStore();

  const handleClickSagaRecord = (record) => {
    history.push(`/asgard/saga-instance${search}&sagaId=${record.get('id')}`);
  };

  const renderSagaCode = ({ value, record }) => (
    <a className="c7n-overview-sage" onClick={() => handleClickSagaRecord(record)}>{value}</a>
  );

  return (
    <div className="c7n-overview-failedStatistics">
      <span className="c7n-overview-failedStatistics-titlespan">失败事物统计</span>
      <Table
        className="c7n-overview-failedStatistics-table"
        dataSet={FailedStatisticsTableDataSet}
        queryBar="none"
      >
        <Column name="sagaCode" renderer={renderSagaCode} />
        <Column name="refType" />
        <Column name="startTime" />
      </Table>
    </div>
  );
}));

export default FailedStatistics;
