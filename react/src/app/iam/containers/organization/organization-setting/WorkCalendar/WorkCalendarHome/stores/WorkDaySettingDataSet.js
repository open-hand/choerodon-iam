export default ({ orgId, calendarDataSet }) => ({
  autoCreate: true,
  autoQuery: true,
  paging: false,
  transport: {
    read: () => ({
      url: `/base/v1/organizations/${orgId}/time_zone_work_calendars`,
      method: 'get',
    }),
    submit: ({ data: [data] }) => ({
      url: `/base/v1/organizations/${orgId}/time_zone_work_calendars/${data.timeZoneId}`,
      method: 'put',
      data,
    }),
  },
  fields: [
    { name: 'areaCode', type: 'string', label: '地区', required: true },
    { name: 'timeZoneCode', type: 'string', label: '时区', required: true },
    { name: 'objectVersionNumber', type: 'string', label: '日历' },
    { name: 'saturdayWork', type: 'boolean', label: '选定周六为工作日' },
    { name: 'sundayWork', type: 'boolean', label: '选定周日为工作日' },
    { name: 'useHoliday', type: 'boolean', label: '自动更新每年的法定节假日' },
  ],
  events: {
    load: ({ dataSet }) => {
      calendarDataSet.setQueryParameter('timeZoneId', dataSet.current.get('timeZoneId'));
      calendarDataSet.query();
    },
  },
});
