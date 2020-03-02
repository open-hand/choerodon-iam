
export default () => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/notify/v1/system_notice/completed?size=3',
      method: 'get',
    },
  },
  fields: [

  ],
});
