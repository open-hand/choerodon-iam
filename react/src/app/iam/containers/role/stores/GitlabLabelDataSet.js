export default () => ({
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: '/base/v1/labels?type=role&level=project&gitlabLabel=true',
      method: 'get',
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
