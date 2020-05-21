
export default ({ organizationId }) => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: `/hmnt/choerodon/v1/${organizationId}/site/audit/operational/logs?size=8`,
      method: 'get',
      transformResponse: (data) => {
        const arr = JSON.parse(data);
        arr.list = arr.content;
        arr.list = arr.list.map((item) => {
          item.display = 'none';
          return item;
        });
        return arr;
      },
    },
  },
  fields: [

  ],
});
