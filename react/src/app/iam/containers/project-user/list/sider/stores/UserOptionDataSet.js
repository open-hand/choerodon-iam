export default ({ id = 0 }) => ({
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/base/v1/projects/${id}/enableUsers`,
      method: 'get',
      params: {
        user_name: '',
      },
    },
  },
  fields: [
    { name: 'realName', type: 'string' },
    { name: 'loginName', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
