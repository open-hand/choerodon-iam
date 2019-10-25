import React, { useContext, useState } from 'react';
import { Content, Page } from '@choerodon/boot';
import { Button, Table } from 'choerodon-ui/pro';
import { Tag, Tooltip } from 'choerodon-ui';

import Store from '../store';
import SagaCodeToolTip from './SagaCodeToolTip';
import './style/index.less';

const { Column } = Table;
const prefixCls = 'c7n-saga-instance';

function ListView() {
  const { sagaDataSet } = useContext(Store);
  const [activeTab, setActiveTab] = useState('instance');

  function renderSagaCode({ text, record }) {
    return (
      <Tooltip placement="bottom" title={<SagaCodeToolTip id={record.id} />}>
        {text}
      </Tooltip>
    );
  }

  function renderTag({ record }) {
    let color = '';
    let text = '';
    if (record.get('status') === 'FAILED') {
      color = '#F44336';
      text = '失败';
    } else if (record.get('status') === 'COMPLETED') {
      text = '完成';
      color = '#00BFA5';
    } else {
      text = '运行中';
      color = '#4D90FE';
    }
    return (
      <Tag color={color}>{text}</Tag>
    );
  }

  return (
    <Page className={`${prefixCls}`}>
      <Content>
        <div className={`${prefixCls}-wrap`}>
          <div>
            <div className={`${prefixCls}-wrap-title`}>
              事务实例
            </div>
            <div className={`${prefixCls}-wrap-btns`}>
              <span className="text">
                查看实例：
              </span>
              <Button
                type="primary"
                funcType="flat"
                className={activeTab === 'instance' && 'active'}
              >
                事务
              </Button>
              <Button
                type="primary"
                funcType="flat"
              >
                任务
              </Button>
            </div>
          </div>
          <div className={`${prefixCls}-wrap-status-content`}>
            <div>
              <div className={`${prefixCls}-wrap-status-text`}>实例状态总览</div>
              <div className={`${prefixCls}-wrap-status-wrapper`}>
                <div className={`${prefixCls}-wrap-status-num ${prefixCls}-wrap-status-completed`}>
                  <div>111</div>
                  <div>完成</div>
                </div>
                <div className={`${prefixCls}-wrap-status-num ${prefixCls}-wrap-status-running`}>
                  <div>222</div>
                  <div>运行中</div>
                </div>
                <div className={`${prefixCls}-wrap-status-num ${prefixCls}-wrap-status-failed`}>
                  <div>333</div>
                  <div>失败</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <Table
          dataSet={sagaDataSet}
        >
          <Column name="sagaCode" renderer={renderSagaCode} />
          <Column name="status" renderer={renderTag} />
          <Column name="startTime" />
          <Column name="refType" />
          <Column name="refId" />
          <Column name="progress" />
        </Table>
      </Content>
    </Page>
  );
}
export default ListView;
