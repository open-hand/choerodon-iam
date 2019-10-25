const taskDataSet = {
  autoQuery: true,
  paging: true,
  pageSize: 10,
  selection: false,
  transport: {
    read: {
      url: 'asgard/v1/sagas/tasks/instances',
      method: 'get',
    },
  },
  queryFields: [
    { name: 'taskInstanceCode', type: 'string', label: '任务编码' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'sagaInstanceCode', type: 'string', label: '所属事务定义' },
    { name: 'description', type: 'string', label: '描述' },
    { name: 'plannedStartTime', type: 'string', label: '实际开始时间' },
    { name: 'actualEndTime', type: 'string', label: '实际结束时间' },
    { name: 'retry', type: 'string', label: '重试次数' },
  ],
  fields: [
    { name: 'taskInstanceCode', type: 'string', label: '任务编码' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'sagaInstanceCode', type: 'string', label: '所属事务定义' },
    { name: 'description', type: 'string', label: '描述' },
    { name: 'plannedStartTime', type: 'string', label: '实际开始时间' },
    { name: 'actualEndTime', type: 'string', label: '实际结束时间' },
    { name: 'retry', type: 'string', label: '重试次数' },
  ],
};

export default taskDataSet;
