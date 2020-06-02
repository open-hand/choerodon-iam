import React, { useEffect, useRef } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Select, message } from 'choerodon-ui/pro';
import { Modal } from 'choerodon-ui';
import { axios } from '@choerodon/boot';
import FormSelectEditor from '../../components/formSelectEditor';

const { Sidebar } = Modal;
export default observer(({ onCancel, onOk, ds, record, organizationId, optionsDataSet }) => {
  const isFirstRender = useRef(true);
  function handleCancel() {
    onCancel();
    ds.reset();
  }
  async function handleOk() {
    try {
      const postData = ds.current.toData();
      postData.memberRoleList = [];
      postData.roles.forEach(v => {
        if (v) {
          postData.memberRoleList.push({ id: v });
        }
      });
      postData.pwdReplayFlag = 0;
      postData.roles = null;
      const result = await axios.put(`/iam/v1/${organizationId}/clients`, JSON.stringify(postData));
      if (result.failed) {
        throw result.message;
      }
    } catch (err) {
      message.error(err);
      return false;
    }
    await ds.query();
    message.info('保存成功');
    await onOk();
  }
  useEffect(() => {
    if (isFirstRender.current) {
      isFirstRender.current = false;
      if (ds.current.get('roles').length === 0) {
        ds.current.set('roles', ['']);
      }
    }
  }, []);
  return (
    <Sidebar
      title={`为客户端"${record.get('name')}"分配角色`}
      bodyStyle={{ padding: '0.24rem 0.2rem' }}
      okCancel
      okText="保存"
      onOk={handleOk}
      onCancel={handleCancel}
      visible
      className="safe-modal"
      width={390}
    >
      <FormSelectEditor
        record={ds.current}
        optionDataSet={optionsDataSet}
        name="roles"
        addButton="添加其他角色"
        maxDisable
      >
        {((itemProps) => (
          <Select
            {...itemProps}
            labelLayout="float"
              // renderer={renderOption}
            searchable
            style={{ width: '100%' }}
          />
        ))}
      </FormSelectEditor>
    </Sidebar>
  );
});
