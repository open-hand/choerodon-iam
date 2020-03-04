import React, { useEffect, Fragment } from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import ReactEchartsCore from 'echarts-for-react/lib/core';
import echarts from 'echarts/lib/echarts';

import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';
import 'echarts/lib/component/legend';
import 'echarts/lib/chart/pie';
import 'echarts/lib/component/markPoint';
import { Spin } from 'choerodon-ui';
import { useOrgOverviewRightSide } from '../../stores';


const PieChart = observer(() => {
  const {
    appServiceDs,
    overStores: { getPieRecord, setPieRecord, getLegendArr, getLeftDataArr },
  } = useOrgOverviewRightSide();

  useEffect(() => {
    if (appServiceDs.current) {
      setPieRecord(appServiceDs.toData());
    }
  }, [appServiceDs.current]);

  function renderChartNumber(data) {
    const { dataIndex, seriesName, name, value } = data;
    // dataIndex 从上到下饼状图数据位置
    if (dataIndex < 9) {
      return `${seriesName}：${name}<br/>应用服务数量：${value}`;
    } else if (dataIndex >= 9) {
      // 大于等于9时将所有超过9的数据合在一起
      return getLeftDataArr.map((item) => `${item.projectName}：${item.appServerSum}`).join('<br/>');
    }
  }


  const getOpts = () => {
    const option = {
      tooltip: {
        trigger: 'item',
        formatter: renderChartNumber, // 自定义label
        padding: 13,
        backgroundColor: 'rgba(255,255,255,1)',
        position: 'bottom',
        textStyle: {
          fontWeight: 400,
          color: 'rgba(58, 52, 95, 0.65)',
          fontSize: '13px',
          lineHeight: '12px',
        },
        extraCssText: 'box-shadow: 0px 2px 6px 0px rgba(0,0,0,0.12);width:auto;height:auto;z-index:999',
      },
      legend: {
        type: 'scroll',
        orient: 'vertical',
        right: '1%',
        icon: 'circle',
        itemHeight: 10,
        selectMode: false,
        data: getLegendArr,
      },
      series: [
        {
          name: '项目名称',
          type: 'pie',
          radius: ['69%', '100%'],
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
          data: getPieRecord,
        },
      ],
    };
    return option;
  };

  const renderChart = () => {
    if (appServiceDs.current && getPieRecord.length > 0) {
      return (
        <div className="c7n-overview-appOverview-pieChart">
          <Fragment>
            <ReactEchartsCore
              echarts={echarts}
              option={getOpts()}
              notMerge
              lazyUpdate
            />
            <Icon type="dashboard" />
          </Fragment>
        </div>
      );
    } else {
      return '此组织应用服务为空';
    }
  };

  return (
    <Fragment>
      {renderChart()}
    </Fragment>
  );
});

export default PieChart;
