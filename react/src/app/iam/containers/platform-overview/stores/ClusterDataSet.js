
export default () => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/devops/v1/clusters/overview',
      method: 'get',
    },
  },
  fields: [

  ],
});
