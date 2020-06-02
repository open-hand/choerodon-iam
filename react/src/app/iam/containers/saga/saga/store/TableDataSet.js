export default {
  autoQuery: true,
  paging: true,
  pageSize: 10,
  selection: false,
  transport: {
    read: {
      url: '/hagd/v1/sagas',
      method: 'get',
    },
  },
  queryFields: [
    { name: 'code', type: 'string', label: '编码' },
    { name: 'service', type: 'string', label: '定义的服务' },
    { name: 'description', type: 'string', label: '描述' },
  ],
  fields: [
    { name: 'code', type: 'string', label: '编码' },
    { name: 'service', type: 'string', label: '定义的服务' },
    { name: 'description', type: 'string', label: '描述' },
  ],
};
