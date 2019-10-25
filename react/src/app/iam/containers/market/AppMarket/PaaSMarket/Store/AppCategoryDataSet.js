export default () => ({
  autoQuery: false,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: '/base/v1/paas_app_market/category',
      method: 'get',
    },
  },
  fields: [
    { name: 'id', type: 'number' },
    { name: 'name', type: 'string' },
    { name: 'code', type: 'string' },
    { name: 'enabled', type: 'boolean' },
  ],
});
