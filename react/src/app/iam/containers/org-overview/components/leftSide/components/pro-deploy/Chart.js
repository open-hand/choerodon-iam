import React, { useEffect, useState } from 'react';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts';

const Charts = () => {
  const [resizeIf, setResizeIf] = useState(false);

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

  const getOption = () => ({
    color: ['#6887E8', '#514FA0', '#F48590'],
    legend: {
      right: 0,
      itemHeight: 10,
      data: [{
        name: 'project A',
        icon: 'circle',
      }, {
        name: 'project B',
        icon: 'circle',
      }, {
        name: 'project C',
        icon: 'circle',
      }],
    },
    grid: {
      top: '30px',
      left: 0,
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
          日期: ${params[0].name}</br>
          较昨日新增: </br>
          总人数: ${params[0].value}
        `;
      },
    },
    xAxis: {
      type: 'category',
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
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
      name: '次数',
      type: 'value',
      axisLabel: { color: 'rgba(0,0,0,0.65)' },
      axisLine: {
        lineStyle: {
          color: '#EEEEEE',
        },
      },
    },
    series: [{
      name: 'project A',
      type: 'bar',
      data: [10, 11, 12, 13, 14, 15, 16],
      itemStyle: {
        barBorderRadius: [4, 4, 0, 0],
      },
      barWidth: 6,
      barGap: '50%',
    }, {
      name: 'project B',
      type: 'bar',
      data: [11, 12, 13, 14, 15, 16, 17],
      barWidth: 6,
      itemStyle: {
        barBorderRadius: [4, 4, 0, 0],
      },
      barGap: '50%',
    }, {
      name: 'project C',
      type: 'bar',
      data: [12, 13, 14, 15, 16, 17, 18],
      barWidth: 6,
      itemStyle: {
        barBorderRadius: [4, 4, 0, 0],
      },
      barGap: '50%',
    }],
  });
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
};

export default Charts;
