export default () => ({
  autoQuery: false,
  selection: 'single',
  transport: {
    read: {
      url: '/base/v1/site/enableUsers',
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
