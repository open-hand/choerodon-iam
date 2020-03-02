
export default ({ organizationId }) => ({
  autoQuery: false,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: `/base/v1/operatelog/${organizationId}/operate/log?size=8`,
      method: 'get',
    },
  },
  fields: [
  
  ],
});
