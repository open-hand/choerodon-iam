import React from 'react';
import { withRouter } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { Tabs } from 'choerodon-ui';
import { Button } from 'choerodon-ui/pro';
import { useLdapStore } from './stores';
import ManualContent from './ManualContent';
import AutoContent from './AutoContent';
import SyncRecord from './SyncRecord';

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
    recordTableDs,
  } = useLdapStore();

  function linkToLDAP() {
    const {
      history,
      location: {
        search,
      },
    } = props;
    history.push(`/iam/organization-setting/ldap${search}`);
  }

  function handleTabChange(value) {
    setTabKey(value);
    if (value === 'record') {
      recordTableDs.query();
      modal.update({
        footer: (okBtn, cancelBtn) => (
          <div>
            <Button
              color="primary"
              funcType="raised"
              onClick={linkToLDAP}
            >
              转至LDAP设置
            </Button>
            {cancelBtn}
          </div>
        ),
      });
    } else if (value === 'auto') {
      modal.update({
        okText: '保存',
        cancelText: '取消',
      });
    } else {
      modal.update({ okText: '手动同步' });
    }
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
        <TabPane tab="同步记录" key="record">
          <SyncRecord />
        </TabPane>
      </Tabs>
    </div>
  );
}));

export default ldapSetting;
