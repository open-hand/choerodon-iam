export default () => ({
  selection: false,
  transport: {
    read: ({ data: { date } }) => ({
      url: `/hagd/v1/sagas/instances/statistics/failure/list?date=${date}`,
      method: 'get',
    }),
  },
  fields: [{
    name: 'sagaCode',
    type: 'string',
    label: '事务实例',
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
