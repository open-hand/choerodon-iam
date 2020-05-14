export default () => ({
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: '/iam/choerodon/v1/site/roles?role_name=&only_select_enable=false',
      method: 'get',
      dataKey: null,
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'code', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
