export default (({ orgId, timeZoneId, year }) => ({
  autoCreate: false,
  autoQuery: false,
  paging: false,
  transport: {
    read: ({ data }) => ({
      url: `/iam/choerodon/v1/organizations/${orgId}/time_zone_work_calendars/ref/${data.timeZoneId}?year=${year}`,
      method: 'get',
      dataKey: null,
    }),
    submit: ({ data: [data] }) => ({
      url: `/iam/choerodon/v1/organizations/${orgId}/time_zone_work_calendars/ref/batch/${data.timeZoneId}?year=${year}`,
      method: 'put',
    }),
  },
  fields: [
    { name: 'status', type: 'number' },
  ],
}));
