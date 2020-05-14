export default ({ level }) => ({
  autoQuery: true,
  selection: 'single',
  paging: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/labels?level=${level}&sort=id%2Cdesc`,
      method: 'get',
      transformResponse(data) {
        const arr = JSON.parse(data);
        return { list: arr };
      },
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'description', type: 'string' },
  ],
});
