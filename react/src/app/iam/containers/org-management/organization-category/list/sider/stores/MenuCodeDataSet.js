export default ({ id }) => ({
  autoQuery: true,
  paging: false,
  selection: false,
  dataKey: null,
  transport: {
    read: {
      url: `/base/v1/categories/org/${id}`,
      method: 'get',
    },
  },
});
