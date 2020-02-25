import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts/lib/echarts';

import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import 'echarts/lib/chart/pie';
import 'echarts/lib/component/markPoint';

const colorStyle = ['#FD818DFF', '#6887E8FF', '#514FA0FF', '#F48590FF', '#CACAE4FF', '#6480DEFF'];

const dataSource = [
  {
    value: 335,
    name: 'project A',
    itemStyle: {
      normal: { color: '#FD818DFF' },
      emphasis: { color: '#FD818DFF' },
    },
  },
  {
    value: 311,
    name: 'project B',
    itemStyle: {
      normal: { color: '#6887E8FF' },
      emphasis: { color: '#6887E8FF' },
    },
  },
  {
    value: 310,
    name: 'project C',
    itemStyle: {
      normal: { color: '#514FA0FF' },
      emphasis: { color: '#514FA0FF' },
    },
  },
  {
    value: 234,
    name: 'project D',
    itemStyle: {
      normal: { color: '#F48590FF' },
      emphasis: { color: '#F48590FF' },
    },
  },
  {
    value: 135,
    name: 'project E',
    itemStyle: {
      normal: { color: '#CACAE4FF' },
      emphasis: { color: '#CACAE4FF' },
    },
  },
  {
    value: 548,
    name: 'project F',
    itemStyle: {
      normal: { color: '#6480DEFF' },
      emphasis: { color: '#6480DEFF' },
    },
  },
];

function handleDataSource() {
  
}

function renderChartNumber(data) {
  const { dataIndex, seriesName, name, value } = data;
  // dataIndex 从上到下饼状图数据位置
  if (dataIndex < 9) {
    return `${seriesName}：${name}<br/>应用服务数量：${value}`;
  } else if (dataIndex >= 9) {
    return null; // 大于等于9时将所有超过9的数据合在一起
  }
}

const PieChart = observer(() => {
  const getOpts = () => {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: renderChartNumber, // 自定义label
        triggerOn: 'click',
        padding: 13,
        backgroundColor: 'rgba(255,255,255,1)',
        textStyle: {
          fontWeight: 400,
          color: 'rgba(58, 52, 95, 0.65)',
          fontSize: '13px',
          lineHeight: '12px',
        },
        extraCssText: 'box-shadow: 0px 2px 6px 0px rgba(0,0,0,0.12);width:159px;height:63px;',
      },
      legend: {
        orient: 'vertical',
        right: '23.5%',
        height: '194px',
        icon: 'circle',
        selectMode: false,
        itemGap: 15, // legend的item各个间隔
        data: ['project A', 'project B', 'project C', 'project D', 'project E', 'project F'],
      },
      // color: ['#FD818DFF', '#6887E8FF', '#514FA0FF', '#F48590FF', '#CACAE4FF', '#6480DEFF'],
      series: [
        {
          name: '项目名称',
          type: 'pie',
          radius: ['62%', '100%'],
          center: ['58.6%', '50%'], // 设置饼图位置
          width: '170px',
          height: '170px',
          startAngle: 165,
          avoidLabelOverlap: false,
          hoverAnimation: false,
          label: {
            normal: {
              show: false,
              position: 'center',
            },
            emphasis: {
              show: true,
              textStyle: {
                fontSize: '30',
                fontWeight: 'bold',
              },
            },
          },
          labelLine: {
            normal: {
              show: false,
            },
          },
          // 数据
          data: dataSource,
        },
      ],
    };
    return option;
  };

  return (
    <div className="c7n-overview-appOverview-pieChart">
      <ReactEchartsCore
        echarts={echarts}
        option={getOpts()}
        notMerge
        lazyUpdate
      />
      <Icon type="dashboard" />
    </div>
  );
});

export default PieChart;
