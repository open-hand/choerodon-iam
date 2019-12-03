export default ({ orgId }) => ({
  autoCreate: false,
  autoQuery: true,
  paging: false,
  transport: {
    read: {
      url: `/base/v1/organizations/${orgId}/ldaps/auto/detail`,
      method: 'get',
    },
    create: ({ data: [data] }) => ({
      url: `/base/v1/organizations/${orgId}/ldaps/auto`,
      method: 'post',
      data,
    }),
    update: ({ data: [data] }) => ({
      url: `/base/v1/organizations/${orgId}/ldaps/auto`,
      method: 'put',
      data,
    }),
  },
  fields: [
    { name: 'active', type: 'boolean', label: '是否自动同步', defaultValue: false, required: true },
    { name: 'frequency', label: '同步频率', type: 'string', required: true },
    { name: 'startTime', label: '开始同步时间', type: 'string', required: true },
  ],
});
