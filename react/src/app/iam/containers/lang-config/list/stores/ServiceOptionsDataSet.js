export default () => ({
  autoQuery: true,
  paging: false,
  selection: 'single',
  transport: {
    read: {
      url: '/iam/choerodon/v1/instances',
      method: 'get',
      dataKey: null,
      transformResponse: ((data) => {
        data = JSON.parse(data).map((v) => ({ service: v }));
        return { list: data };
      }),
    },
  },
  queryFields: [
    { name: 'service', type: 'string' },
  ],
});
