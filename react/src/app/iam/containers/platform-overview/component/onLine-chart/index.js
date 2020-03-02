import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts/lib/echarts';
import './index.less';

import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import 'echarts/lib/chart/line';
import 'echarts/lib/component/markPoint';
import { usePlatformOverviewStore } from '../../stores';


const LineChart = observer(() => {
  const {
    onlineNumDs,
    onlineHourDs,
  } = usePlatformOverviewStore();

  const record = onlineHourDs.current && onlineHourDs.toData()[0];

  const getOpts = () => {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: '时间：{b}<br/>在线人数：{c}',
        padding: 13,
        backgroundColor: 'rgba(255,255,255,1)',
        textStyle: {
          fontWeight: 400,
          color: '#3A345FA6',
          fontSize: '13px',
          lineHeight: '12px',
        },
        extraCssText: 'box-shadow: 0px 2px 6px 0px rgba(0,0,0,0.12);width:156px;height:63px;',
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: record && Object.keys(record),
        show: false,
      },
      yAxis: {
        type: 'value',
        show: false,
      },
      series: [{
        smooth: true,
        data: record && Object.values(record),
        type: 'line',
        color: ['rgb(168, 185, 237)'],
        symbolSize: 5,
        symbol: 'circle',
        areaStyle: {
          color: '#6887E8C2',
        },
        itemStyle: {
          normal: {
            // color:'#6887E8C2',
            // borderColor:'#6887E8C2',
          },
          emphasis: {
            color: '#CACAE4FF',
            borderColor: '#CACAE4FF',
          },
        },
      }],
      grid: {
        top: '31px', // 与容器顶部的距离
        height: 79,
        x: 0,
        x2: 0,
        y2: '22px',
      },
    };
    return option;
  };

  return (
    <div className="c7n-online-chart">
      <div className="c7n-online-number">
        <span>{onlineNumDs.current && onlineNumDs.current.get('OnlineCount')}</span>
        <span>人</span>
      </div>
      <div className="c7n-online-mainChart">
        <ReactEchartsCore
          echarts={echarts}
          option={getOpts()}
          notMerge
          lazyUpdate
        />
      </div>
      <div className="c7n-online-daily">
        <span>日访问量：</span>
        <span>{onlineNumDs.current && onlineNumDs.current.get('NumberOfVisitorsToday')}人</span>
      </div>
    </div>
  );
});
export default LineChart;
