export default () => ({
  autoQuery: false,
  paging: false,
  fields: [
    { name: 'code', type: 'string', label: '权限' },
    { name: 'description', type: 'string', label: '描述' },
  ],
  queryFields: [
    { name: 'params', type: 'string' },
  ],
});
