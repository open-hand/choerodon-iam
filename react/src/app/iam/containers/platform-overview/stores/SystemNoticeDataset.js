
export default () => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: '/notify-service/v1/system_notice/completed?size=3',
      method: 'get',
    },
  },
  fields: [

  ],
});
