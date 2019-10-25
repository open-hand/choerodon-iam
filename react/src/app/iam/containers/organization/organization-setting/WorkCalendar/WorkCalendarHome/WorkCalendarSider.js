import React, { Component } from 'react';
import { observer } from 'mobx-react-lite';
import { Form, SelectBox, CheckBox, Select } from 'choerodon-ui/pro';
import './index.less';
import WorkCalendar from './Component/WorkCalendar';

const { Option } = SelectBox;

export default observer(({ context, modal }) => {
  const { calendarDataSet, holidayDataSet, workDaySettingDataSet } = context;
  const saturdayWork = workDaySettingDataSet.current && workDaySettingDataSet.current.get('saturdayWork');
  const sundayWork = workDaySettingDataSet.current && workDaySettingDataSet.current.get('sundayWork');
  const useHoliday = workDaySettingDataSet.current && workDaySettingDataSet.current.get('useHoliday');
  const timeZoneId = workDaySettingDataSet.current && workDaySettingDataSet.current.get('timeZoneId');
  function handleChange(data) {
    const findData = calendarDataSet.find((record) => record.get('workDay') === data.workDay);
    if (!findData) {
      calendarDataSet.create({ ...data, timeZoneId });
    } else {
      calendarDataSet.remove(findData);
    }
  }
  async function refresh() {
    await workDaySettingDataSet.query();
    holidayDataSet.query();
  }
  async function handleOk() {
    try {
      await workDaySettingDataSet.submit();
      await calendarDataSet.submit();
      refresh();
    } catch (err) {
      return false;
    }
  }
  modal.handleCancel(() => {
    calendarDataSet.reset();
  });
  modal.handleOk(handleOk);
  return (
    <div>
      <Form dataSet={workDaySettingDataSet} style={{ width: '5.12rem' }}>
        <Select name="areaCode">
          <Option value="Asia">亚洲</Option>
        </Select>
        <Select name="timeZoneCode">
          <Option value="Asia/Shanghai">(GMT+08:00) Shanghai</Option>
        </Select>
        <div style={{ fontSize: '.15rem' }}>日历设置</div>
        <CheckBox name="useHoliday" />
        <CheckBox name="saturdayWork" />
        <CheckBox name="sundayWork" />
      </Form>
      <WorkCalendar
        saturdayWork={saturdayWork}
        sundayWork={sundayWork}
        useHoliday={useHoliday}
        selectDays={calendarDataSet.toData()}
        holidayRefs={holidayDataSet.toData()}
        updateSelete={handleChange}
      />
    </div>
  );
});
