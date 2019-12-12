export default ({ orgId }) => ({
  autoQuery: false,
  paging: false,
  transport: {
    read: {
      url: `/base/v1/organizations/${orgId}/ldaps/latest_history`,
      method: 'get',
      dataKey: null,
    },
  },
  fields: [
    { name: 'newUserCount', label: '同步用户的数量', type: 'number', defaultValue: 0 },
    { name: 'syncBeginTime', label: '上次同步的时间', type: 'string' },
    { name: 'syncEndTime', label: '同步结束时间', type: 'string' },
    { name: 'ldapId', label: '同步id', type: 'number' },
  ],
});
