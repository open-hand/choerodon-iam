import React, { useEffect, useState } from 'react';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts';
import { observer } from 'mobx-react-lite';
import { useFailedStatisticsStore } from './stores';

const Charts = observer(() => {
  const [resizeIf, setResizeIf] = useState(false);

  const {
    ThingPerformStore,
  } = useFailedStatisticsStore();

  useEffect(() => {
    function resizeCharts() {
      setResizeIf(true);
      setTimeout(() => {
        setResizeIf(false);
      }, 500);
    }
    window.addEventListener('resize', () => {
      resizeCharts();
    });
    resizeCharts();
    return () => {
      window.removeEventListener('resize', () => {});
    };
  }, []);

  const getOption = () => {
    const {
      x,
      y,
      percentage,
      totalCount,
    } = ThingPerformStore.getChartData;
    return {
      grid: {
        top: '30px',
        left: '5px',
        right: '50px',
        bottom: 0,
        containLabel: true,
      },
      tooltip: {
        trigger: 'axis',
        position(pt) {
          return [pt[0], '10%'];
        },
        backgroundColor: 'rgba(255,255,255,1)',
        textStyle: {
          color: 'rgba(58,52,95,0.65)',
        },
        extraCssText: 'box-shadow:0px 2px 6px 0px rgba(0,0,0,0.12);padding: 15px 17px;',
        formatter(params) {
          return `
          日期: ${`${x[0].split('-')[0]}-${params[0].name}`}</br>
          事务失败率: ${percentage[params[0].dataIndex]}%</br>
          失败次数: ${params[0].value}</br>
          总次数: ${totalCount[params[0].dataIndex]}
        `;
        },
      },
      xAxis: {
        boundaryGap: false,
        type: 'category',
        data: x.map(d => `${d.split('-')[1]}-${d.split('-')[2]}`),
        name: '时间',
        nameTextStyle: {
          color: 'rgba(0,0,0,1)',
          fontSize: '13px',
        },
        splitLine: {
          show: true,
        },
        axisLabel: { color: 'rgba(0,0,0,0.65)' },
        axisLine: {
          lineStyle: {
            color: '#EEEEEE',
          },
        },
        axisTick: {
          alignWithLabel: true,
        },
      },
      yAxis: {
        nameTextStyle: {
          color: 'rgba(0,0,0,1)',
          fontSize: '13px',
        },
        name: '失败次数',
        type: 'value',
        axisLabel: { color: 'rgba(0,0,0,0.65)' },
        axisLine: {
          lineStyle: {
            color: '#EEEEEE',
          },
        },
      },
      series: [{
        data: y,
        type: 'line',
        smooth: true,
        symbol: 'circle',
        areaStyle: {
          normal: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
              offset: 0,
              color: 'rgba(244, 133, 144, 0.3)',
            }, {
              offset: 1,
              color: 'rgba(244, 133, 144, 0)',
            }]),
          },
        },
        itemStyle: {
          normal: {
            color: '#F48590', // 改变折线点的颜色
          // lineStyle:{
          //   color:'#8cd5c2' //改变折线颜色
          // }
          },
        },
      }],
    };
  };
  return !resizeIf ? (
    <ReactEchartsCore
      echarts={echarts}
      option={getOption()}
      notMerge
      onChartReady={(chart) => {
        setTimeout(() => {
          chart.resize();
        }, 1000);
      }}
      style={{
        width: '100%',
        height: 216,
      }}
      lazyUpdate
    />
  ) : '';
});

export default Charts;
