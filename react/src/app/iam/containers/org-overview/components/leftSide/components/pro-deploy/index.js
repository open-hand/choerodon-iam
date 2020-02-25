import React from 'react';
import { observer } from 'mobx-react-lite';
import { Select } from 'choerodon-ui/pro';
import ContainerBlock from '../../../ContainerBlock';
import Chart from './Chart';

import './index.less';

const { Option } = Select;

const ProDeploy = observer(() => {
  const handleChangeDays = (days) => {
    window.console.log(days);
  };

  const SelectProList = () => (
    <Select>
      <Option value={1}>1</Option>
    </Select>
  );

  return (
    <div className="c7n-overview-prodeploy">
      <ContainerBlock
        width="100%"
        height={306}
        title="项目部署情况"
        hasDaysPicker
        titleExtra={SelectProList()}
        handleChangeDays={handleChangeDays}
      >
        <Chart />
      </ContainerBlock>
    </div>
  );
});

export default ProDeploy;
