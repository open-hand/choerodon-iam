import React, { useEffect, useState } from 'react';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import { observer } from 'mobx-react-lite';
import echarts from 'echarts';
import { useEmailSendStore } from './stores';

const Charts = observer(() => {
  const [resizeIf, setResizeIf] = useState(false);

  const {
    EmailSendStore,
  } = useEmailSendStore();

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
      dates,
      successNums,
      failedNums,
      totalNums,
    } = EmailSendStore.getEmailSendData;

    const totalSuccess = successNums.length > 0 ? successNums.reduce((total, currentValue) => total + currentValue) : '';
    const totalFailed = failedNums.length > 0 ? failedNums.reduce((total, currentValue) => total + currentValue) : '';

    return {
      color: ['#6887E8', '#F48590'],
      legend: {
        right: 0,
        itemHeight: 10,
        data: [{
          name: `成功次数: ${totalSuccess}`,
          icon: 'circle',
        }, {
          name: `失败次数: ${totalFailed}`,
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
            日期: ${`${dates[0].split('-')[0]}-${params[0].name}`}</br>
            成功发送数目: ${params[0].data}</br>
            失败发送数目: ${params[1].data}</br>
            发送总数: ${totalNums[params[0].dataIndex]}</br>
            发送成功率 ${totalNums[params[0].dataIndex] !== 0 ? ((params[0].data / (totalNums[params[0].dataIndex])) * 100).toFixed(1) : 0}%
          `;
        },
      },
      xAxis: {
        boundaryGap: false,
        data: dates.map(d => `${d.split('-')[1]}-${d.split('-')[2]}`),
        name: '时间',
        nameTextStyle: {
          color: 'rgba(0,0,0,1)',
          fontSize: '13px',
        },
        splitLine: {
          show: true,
        },
        axisLabel: {
          color: 'rgba(0,0,0,0.65)',
        },
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
      series: [
        {
          name: `成功次数: ${totalSuccess}`,
          type: 'bar',
          stack: 'one',
          data: successNums,
          itemStyle: {
            barBorderRadius: [0, 0, 0, 0],
          },
          barWidth: 8,
        },
        {
          name: `失败次数: ${totalFailed}`,
          type: 'bar',
          stack: 'one',
          data: failedNums,
          itemStyle: {
            barBorderRadius: [4, 4, 0, 0],
          },
          barWidth: 8,
        },
      ],
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
        height: 300,
      }}
      lazyUpdate
    />
  ) : '';
});

export default Charts;
