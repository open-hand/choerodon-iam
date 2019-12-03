import React, { useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, SelectBox, Select, DateTimePicker } from 'choerodon-ui/pro';
import { useLdapStore } from './stores';

const { Option } = SelectBox;

const autoContent = observer((props) => {
  const {
    syncRecordDs,
    syncFormDs,
    prefixCls,
    modal,
    ldapStore,
  } = useLdapStore();

  useEffect(() => {
    if (ldapStore.getTabKey === 'auto') {
      modal.handleOk(async () => {
        try {
          if (await syncFormDs.submit() !== false) {
            return true;
          } else {
            return false;
          }
        } catch (e) {
          return false;
        }
      });
    }
  }, [ldapStore.getTabKey]);
  
  return (
    <div className={`${prefixCls}-auto-content`}>
      <Form dataSet={syncFormDs}>
        <SelectBox name="active">
          <Option value>是</Option>
          <Option value={false}>否</Option>
        </SelectBox>
        <Select name="frequency">
          <Option value="DAY">一天一次</Option>
          <Option value="WEEK">一周一次</Option>
          <Option value="MONTH">一月一次</Option>
        </Select>
        <DateTimePicker name="startTime" />
      </Form>
    </div>
  );
});

export default autoContent;
