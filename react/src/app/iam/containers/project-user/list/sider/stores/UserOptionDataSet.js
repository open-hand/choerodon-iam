export default ({ id = 0 }) => ({
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/projects/${id}/enableUsers`,
      method: 'get',
      params: {
        user_name: '',
      },
    },
  },
  fields: [
    { name: 'realName', type: 'string' },
    { name: 'loginName', type: 'string' },
    { name: 'id', type: 'string', unique: true },
  ],
});
