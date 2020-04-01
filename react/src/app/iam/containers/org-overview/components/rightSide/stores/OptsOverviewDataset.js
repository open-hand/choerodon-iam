
export default ({ organizationId }) => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: `/base/v1/organization/${organizationId}/operate/log?size=8`,
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
