export default ({ id = 0, intl, intlPrefix }) => ({
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: '/base/v1/site/roles?role_name=&only_select_enable=true',
      method: 'get',
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'code', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
