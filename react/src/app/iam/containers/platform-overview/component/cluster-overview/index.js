import React, { useEffect } from 'react';
import './index.less';
import { Icon } from 'choerodon-ui';
import { observer } from 'mobx-react-lite';
import { usePlatformOverviewStore } from '../../stores';


const clusterType = [
  {
    code: 'connectedClusters',
    text: '运行中集群',
  },
  {
    code: 'unconnectedCluster',
    text: '未连接集群',
  },
];


export default observer(() => {
  const {
    clusterDs,
  } = usePlatformOverviewStore();

  const record = clusterDs.current;

  useEffect(() => {

  }, []);

  const renderContent = () => (
    clusterType.map((item) => (
      <div className="c7n-platform-clusterOverview-right-content" key={item.code}>
        <span>{item.text}</span>
        <span>{record ? record.get(item.code) : '-'}</span>
      </div>
    ))
  );

  return (
    <div className="c7n-platform-clusterOverview">
      <div className="c7n-platform-clusterOverview-left">
        <Icon type="cluster" />
      </div>
      <div className="c7n-platform-clusterOverview-right">
        {renderContent()}
      </div>
    </div>
  );
});
