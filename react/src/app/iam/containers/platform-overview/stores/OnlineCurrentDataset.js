
export default () => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/devops/v1/online/current',
      method: 'get',
    },
  },
  fields: [

  ],
});
