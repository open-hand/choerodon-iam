import React from 'react';
import { observer } from 'mobx-react-lite';
import {
  Form, Select, SelectBox, NumberField, TextField,
} from 'choerodon-ui/pro';
import { Divider, Modal } from 'choerodon-ui';
import '../index.less';

const { Option } = Select;
const { Sidebar } = Modal;

export default observer(({ dataSet, onCancel, onOk }) => {
  const { current } = dataSet;

  async function handleOk() {
    if (!current.dirty) {
      onOk();
    }

    if (await dataSet.submit()) {
      await dataSet.query();
      await onOk();
    }
  }
  function handleCancel() {
    onCancel();
    dataSet.reset();
  }
  function getSecurity() {
    const ret = [];
    if (current && current.get('enableSecurity')) {
      ret.push(
        (
          <SelectBox name="enableCaptcha" label="是否开启验证码" colSpan={6}>
            <Option value key="yes">是</Option>
            <Option value={false} key="no">否</Option>
          </SelectBox>
        ),
      );
      if (current.get('enableCaptcha')) {
        ret.push(
          (
            <NumberField name="maxCheckCaptcha" label="输错次数" colSpan={6} />
          ),
        );
      }
      ret.push(
        (
          <SelectBox name="enableLock" label="是否开启锁定" colSpan={6}>
            <Option value key="yes">是</Option>
            <Option value={false} key="no">否</Option>
          </SelectBox>
        ),
      );
      if (current.get('enableLock')) {
        ret.push(
          [
            <NumberField name="maxErrorTime" label="输错次数" colSpan={6} />,
            <NumberField name="lockedExpireTime" label="锁定时长" suffix="秒" colSpan={6} />,
          ],
        );
      }
    }
    return ret;
  }
  return (
    <Sidebar
      title="修改密码策略"
      bodyStyle={{ padding: 0 }}
      okCancel
      okText="保存"
      onOk={handleOk}
      onCancel={handleCancel}
      visible
      className="safe-modal"
    >
      <Form style={{ margin: '0 0.15rem', marginTop: '0.24rem' }} className="safe-modal-form" dataSet={dataSet} columns={6}>
        <div className="form-title" colSpan={6}>密码安全策略</div>
        <SelectBox name="enablePassword" label="是否启用：" colSpan={6} className="safe-select">
          <Option value key="yes">是</Option>
          <Option value={false} key="no">否</Option>
        </SelectBox>

        { dataSet.current && dataSet.current.get('enablePassword')
          ? (

            [
              <SelectBox name="forceModifyPassword" label="登录时强制修改默认密码" colSpan={6}>
                <Option value key="yes">是</Option>
                <Option value={false} key="no">否</Option>
              </SelectBox>,
              <SelectBox name="notUsername" label="是否允许密码与登录名相同" colSpan={6}>
                <Option value key="yes">是</Option>
                <Option value={false} key="no">否</Option>
              </SelectBox>,
              <TextField name="originalPassword" label="新用户默认密码" colSpan={6} />,
              <NumberField step={1} name="minLength" label="最小密码长度" colSpan={3} />,
              <NumberField name="maxLength" className="pwdpolicy-max-length" label="最大密码长度" colSpan={3} />,
              <NumberField name="digitsCount" label="最少数字数" colSpan={2} />,
              <NumberField name="lowercaseCount" label="最少小写字母数" colSpan={2} />,
              <NumberField name="uppercaseCount" label="最少大写字母数" colSpan={2} />,
              <NumberField name="specialCharCount" label="最少特殊字符" colSpan={3} />,
              <NumberField name="notRecentCount" label="最大近期密码" colSpan={3} />,
              <TextField name="regularExpression" label="密码正则" colSpan={6} />]
          ) : null}
      </Form>
      <Divider className="divider" colSpan={6} />
      <Form style={{ margin: '0 0.15rem' }} className="safe-modal-form" dataSet={dataSet} columns={6}>
        <div className="form-title" colSpan={6}>登录安全策略</div>
        <SelectBox name="enableSecurity" label="是否启用:" colSpan={6} className="addLine">
          <Option value key="yes">是</Option>
          <Option value={false} key="no">否</Option>
        </SelectBox>
        {getSecurity()}
      </Form>
    </Sidebar>
  );
});
