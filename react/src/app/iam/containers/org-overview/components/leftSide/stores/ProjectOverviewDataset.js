
export default ({ organizationId }) => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: `/base/v1/organizations/${organizationId}/project/overview`,
      method: 'get',
    },
  },
  fields: [

  ],
});
