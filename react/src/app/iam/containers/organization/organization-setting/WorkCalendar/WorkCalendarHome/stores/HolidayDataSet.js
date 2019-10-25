export default ({ orgId, year }) => ({
  autoCreate: true,
  autoQuery: true,
  paging: false,
  transport: {
    read: () => ({
      url: `/base/v1/organizations/${orgId}/work_calendar_holiday_refs?year=${year}`,
      method: 'get',
    }),
  },
  fields: [
    { name: 'status', type: 'number' },
  ],
});
