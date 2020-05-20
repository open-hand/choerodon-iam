
export default () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: 'hmnt/choerodon/v1/0/site/audit/operational/logs?size=5',
      method: 'get',
      transformResponse: (data) => {
        const arr = JSON.parse(data);
        arr.list = arr.content;
        arr.list = arr.list.map(a => {
          a.display = 'none';
          return a;
        });
        return arr;
      },
    },
  },
  fields: [

  ],
});
