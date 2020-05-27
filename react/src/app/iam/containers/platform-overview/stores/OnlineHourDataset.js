
export default () => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/hmsg/choerodon/v1/online/current/list',
      method: 'get',
    },
  },
  fields: [

  ],
});
