import React, { useEffect } from 'react';
import { Tabs } from 'choerodon-ui';
import SagaStore from '../store/SagaImgStore';
import SagaImg from './SagaImg';
import SagaJson from './SagaJson';
import './style/index.less';

const { TabPane } = Tabs;

function SagaDetails(props) {
  useEffect(() => {
    SagaStore.loadDetailData(props.id);
    SagaStore.setData();
  });

  return (
    <div className="sidebar-content">
      <Tabs>
        <TabPane tab="事务定义图" key="1"><SagaImg /></TabPane>
        <TabPane tab="json" key="2"><SagaJson /></TabPane>
      </Tabs>
    </div>
  );
}

export default SagaDetails;
