import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Select } from 'choerodon-ui/pro';
import ContainerBlock from '../../../ContainerBlock';
import { useProDeployStore } from './stores';
import Chart from './Chart';

import './index.less';

const { Option } = Select;

const ProDeploy = observer(() => {
  const {
    ProDeployStore,
    AppState: {
      menuType: { orgId },
    },
    ProDeploySelectDataSet,
  } = useProDeployStore();

  const projectsArray = ProDeployStore.getProjectsArray;

  useEffect(() => {
    ProDeployStore.initProjectsCanChose(orgId).then((res) => {
      ProDeployStore.setProjectArray(res);
      const initSelected = JSON.parse(JSON.stringify(res)).splice(0, 3).map(i => i.id);
      ProDeploySelectDataSet.loadData([{ proSelect: initSelected }]);
      ProDeployStore.initData(orgId, initSelected);
    });
  }, []);

  const handleChangeDays = (days) => {
    ProDeployStore.setChosenDay(days);
    ProDeployStore.initData(orgId, ProDeploySelectDataSet.current.get('proSelect'));
  };

  const SelectProList = () => (
    <Select
      dataSet={ProDeploySelectDataSet}
      name="proSelect"
      searchable
      maxTagCount={2}
    >
      {
        projectsArray.map(p => (
          <Option value={p.id}>{p.name}</Option>
        ))
      }
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
