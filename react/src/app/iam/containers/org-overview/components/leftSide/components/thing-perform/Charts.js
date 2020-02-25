import React from 'react';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts';

const Charts = () => {
  const getOption = () => ({
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
      data: [820, 932, 901, 934, 1290, 1330, 1320],
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
  });
  return (
    <ReactEchartsCore
      echarts={echarts}
      option={getOption()}
      notMerge
      style={{
        width: '100%',
        height: 216,
      }}
      lazyUpdate
    />
  );
};

export default Charts;
