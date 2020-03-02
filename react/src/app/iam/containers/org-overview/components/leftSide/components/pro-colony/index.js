import React from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import ContainerBlock from '../../../ContainerBlock';
import { useOrgOverviewLeftSide } from '../../stores';
import './index.less';

const ClusterType = [
  {
    text: '运行中集群',
    code: 'connectedClusters',
  },
  {
    text: '未连接集群',
    code: 'unconnectedCluster',
  },
];

const ProjectType = [
  {
    text: '启用项目',
    code: 'enableSum',
  },
  {
    text: '停用项目',
    code: 'stopSum',
  },
];

const ProColony = observer(() => {
  const {
    clusterDs,
    projectDs,
  } = useOrgOverviewLeftSide();

  const clusterRecord = clusterDs.current;
  const projectRecord = projectDs.current;

  const renderClusterDetail = () => (
    ClusterType.map((item) => (
      <div className="c7n-overview-content-number_group-item">
        <span>{clusterRecord ? clusterRecord.get(item.code) : '-'}</span>
        <span>{item.text}</span>
      </div>
    ))
  );

  const renderProjectDetail = () => (
    ProjectType.map((item) => (
      <div className="c7n-overview-content-number_group-item">
        <span>{projectRecord ? projectRecord.get(item.code) : '-'}</span>
        <span>{item.text}</span>
      </div>
    ))
  );

  return (
    <div className="c7n-overview-procolony">
      <ContainerBlock width="49%" height={140}>
        <div className="c7n-overview-content">
          <Icon type="project_group" />
          <div className="c7n-overview-number_group">
            {renderProjectDetail()}
          </div>
        </div>
      </ContainerBlock>
      <ContainerBlock width="49%" height={140}>
        <div className="c7n-overview-content">
          <Icon type="cluster" />
          <div className="c7n-overview-number_group">
            {renderClusterDetail()}
          </div>
        </div>
      </ContainerBlock>
    </div>
  );
});

export default ProColony;
