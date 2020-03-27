
export default () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/base/v1/site/0/operate/log?size=5',
      method: 'get',
      transformResponse: (data) => {
        const arr = JSON.parse(data);
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
