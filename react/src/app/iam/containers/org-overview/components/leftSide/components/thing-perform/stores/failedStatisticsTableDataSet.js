export default ({ orgId }) => ({
  selection: false,
  transport: {
    read: ({ data: { date } }) => ({
      url: `/asgard/v1/sagas/organizations/${orgId}/instances/statistics/failure/list?date=${date}`,
      method: 'get',
    }),
  },
  fields: [{
    name: 'sagaCode',
    type: 'string',
    label: '事物实例',
  }, {
    name: 'refType',
    type: 'string',
    label: '关联业务类型',
  }, {
    name: 'startTime',
    type: 'string',
    label: '开始时间',
  }],
});
