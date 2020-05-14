export default ({ id }) => ({
  autoQuery: true,
  paging: false,
  selection: false,
  dataKey: null,
  transport: {
    read: {
      url: `/iam/choerodon/v1/categories/org/${id}`,
      method: 'get',
    },
  },
});
