import React from 'react';
import { withRouter } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { useLdapStore } from './stores';
import ManualContent from './ManualContent';
import AutoContent from './AutoContent';

import './index.less';

const { TabPane } = Tabs;

const ldapSetting = withRouter(observer((props) => {
  const {
    modal,
    prefixCls,
    ldapStore: {
      getTabKey,
      setTabKey,
    },
  } = useLdapStore();
  
  function handleTabChange(value) {
    setTabKey(value);
    modal.update({ okText: value === 'manual' ? '手动同步' : '保存' });
  }
  
  return (
    <div className={`${prefixCls}-content`}>
      <Tabs
        activeKey={getTabKey}
        onChange={handleTabChange}
      >
        <TabPane tab="手动同步" key="manual">
          <ManualContent />
        </TabPane>
        <TabPane tab="自动同步" key="auto">
          <AutoContent />
        </TabPane>
      </Tabs>
    </div>
  );
}));

export default ldapSetting;
