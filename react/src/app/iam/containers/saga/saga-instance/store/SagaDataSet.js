const sagaDataSet = {
  autoQuery: true,
  paging: true,
  pageSize: 10,
  selection: false,
  transport: {
    read: {
      url: 'hagd/v1/sagas/instances',
      method: 'get',
    },
  },
  queryFields: [
    { name: 'sagaCode', type: 'string', label: '事务实例' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'startTime', type: 'string', label: '开始时间' },
    { name: 'refType', type: 'string', label: '关联业务类型' },
    { name: 'refId', type: 'string', label: '关联业务ID' },
    { name: 'progress', type: 'string', label: '进度条' },
  ],
  fields: [
    { name: 'sagaCode', type: 'string', label: '事务实例' },
    { name: 'status', type: 'string', label: '状态' },
    { name: 'startTime', type: 'string', label: '开始时间' },
    { name: 'refType', type: 'string', label: '关联业务类型' },
    { name: 'refId', type: 'string', label: '关联业务ID' },
    { name: 'progress', type: 'string', label: '进度条' },
  ],
};

export default sagaDataSet;
