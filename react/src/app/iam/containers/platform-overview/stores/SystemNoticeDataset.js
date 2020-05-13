
export default () => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/hmsg/choerodon/v1/system_notice/completed?size=3',
      method: 'get',
      transformResponse: (data) => {
        const arr = JSON.parse(data);
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
