import React, { Component, useState } from 'react';
import { SelectBox, Row, Col, TextArea, Select } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';

const { Option } = SelectBox;

const rejectReasons = ['用户信息有误', '所在公司信息有误', '企业网站有误', '注册信息存在敏感信息', '其他'];
const scaleList = {
  1: '1-100',
  100: '100-300',
  300: '300-500',
  500: '500-1000',
  1000: '1000-3000',
  3000: '3000-5000',
  5000: '5000人以上',
};

export default observer(({ record, modal, intl }) => {
  const { dataSet } = record;
  const approvalStatus = record.get('approvalStatus');
  const approvalMessage = record.get('approvalMessage');
  const [other, setOther] = useState('');
  async function handleOk() {
    if (record.getPristineValue('approvalStatus') !== 'no_approval') {
      return true;
    }
    try {
      dataSet.current.set('approvalMessage', other);
      await dataSet.submit();
      await dataSet.query();
    } catch (err) {
      return false;
    }
  }
  modal.handleOk(handleOk);
  modal.handleCancel(() => dataSet.reset());

  function renderApprovalDetail() {
    return (
      <div className="c7n-approval-sider-content">
        <h3 className="c7n-approval-sider-title">审批情况</h3>
        <Row gutter={10}>
          <Col span={8}>
            <span className="c7n-approval-sider-label">审批结果</span>
          </Col>
          <Col span={16}>
            <span className={`c7n-approval-sider-value ${approvalStatus === 'approved' ? 'approved' : 'rejected'}`}>{approvalStatus === 'approved' ? '通过' : '不通过'}</span>
          </Col>
        </Row>
        <Row gutter={10}>
          <Col span={8}>
            <span className="c7n-approval-sider-label">审批时间</span>
          </Col>
          <Col span={16}>
            <span className="c7n-approval-sider-value">{record.get('approvalDate')}</span>
          </Col>
        </Row>
        {approvalStatus === 'rejected' && (
        <Row gutter={10}>
          <Col span={8}>
            <span className="c7n-approval-sider-label">不通过原因</span>
          </Col>
          <Col span={16}>
            <span className="c7n-approval-sider-value">{approvalMessage}</span>
          </Col>
        </Row>
        )}
      </div>
    );
  }

  return (
    <div className="c7n-approval-sider-form">
      <div className="c7n-approval-sider-content">
        <h3 className="c7n-approval-sider-title">申请信息</h3>
        {['userName', 'userEmail', 'userPhone', 'orgName', 'orgBusinessType', 'userOrgPosition', 'orgScale', 'orgHomePage'].map(v => (
          <Row gutter={10}>
            <Col span={8}>
              <span className="c7n-approval-sider-label">{intl.formatMessage({ id: `register.approval.fields.${v.toLowerCase()}` })}</span>
            </Col>
            <Col span={16}>
              {v === 'orgScale' ? <span className="c7n-approval-sider-value">{scaleList[record.get(v)] || record.get(v) || '无'}人</span>
                : <span className="c7n-approval-sider-value">{record.get(v) || '无'}</span>}

            </Col>
          </Row>
        ))}
      </div>
      <div className={`devider ${record.getPristineValue('approvalStatus') === 'no_approval' ? '' : 'mb-20'}`} />
      <div className="c7n-approval-sider-content">
        {record.getPristineValue('approvalStatus') === 'no_approval' && (
        <SelectBox label="是否通过审批" onChange={value => record.set('approvalStatus', value)} value={approvalStatus} labelLayout="float">
          <Option value="approved">通过</Option>
          <Option value="rejected">不通过</Option>
        </SelectBox>
        )}
      </div>
      {approvalStatus === 'rejected' && record.getPristineValue('approvalStatus') === 'no_approval' && (
        <React.Fragment>
          <div className="c7n-approval-sider-content">
            <Select style={{ width: '100%' }} labelLayout="float" label="不通过原因" onChange={value => record.set('approvalMessage', value)} value={approvalMessage}>
              {rejectReasons.map(v => (
                <Option value={v}>{v}</Option>
              ))}
            </Select>
            {approvalMessage === '其他' && (
            <TextArea resize="vertical" rows={5} className="c7n-approval-sider-content-textarea" placeholder="请输入其他原因" value={other} onChange={setOther} labelLayout="float" cols={40} />
            )}
          </div>
        </React.Fragment>
      )}
    
      {record.getPristineValue('approvalStatus') !== 'no_approval' && renderApprovalDetail()}
    </div>
  );
});
