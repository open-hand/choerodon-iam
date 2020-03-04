
export default () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/base/v1/site/0/operate/log?size=5',
      method: 'get',
    },
  },
  fields: [

  ],
});
