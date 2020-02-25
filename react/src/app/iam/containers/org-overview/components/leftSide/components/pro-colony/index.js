import React from 'react';
import { observer } from 'mobx-react-lite';
import { Icon } from 'choerodon-ui';
import ContainerBlock from '../../../ContainerBlock';

import './index.less';


const ProColony = observer(() => (
  <div className="c7n-overview-procolony">
    <ContainerBlock width="49%" height={140}>
      <div className="c7n-overview-content">
        <Icon type="project_group" />
        <div className="c7n-overview-number_group">
          <div className="c7n-overview-content-number_group-item">
            <span>22</span>
            <span>启用项目</span>
          </div>
          <div className="c7n-overview-content-number_group-item">
            <span>22</span>
            <span>停用项目</span>
          </div>
        </div>
      </div>
    </ContainerBlock>
    <ContainerBlock width="49%" height={140}>
      <div className="c7n-overview-content">
        <Icon type="cluster" />
        <div className="c7n-overview-number_group">
          <div className="c7n-overview-content-number_group-item">
            <span>22</span>
            <span>运行中集群</span>
          </div>
          <div className="c7n-overview-content-number_group-item">
            <span>22</span>
            <span>未连接集群</span>
          </div>
        </div>
      </div>
    </ContainerBlock>
  </div>
));

export default ProColony;
