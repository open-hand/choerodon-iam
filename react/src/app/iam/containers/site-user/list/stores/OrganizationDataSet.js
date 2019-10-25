export default () => ({
  autoQuery: false,
  paging: false,
  selection: 'single',
  transport: {
    read: {
      url: '/base/v1/organizations/listByName',
      method: 'get',
      params: {
        organization_name: '',
      },
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'id', type: 'number' },
  ],
});
