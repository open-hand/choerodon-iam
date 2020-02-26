
export default ({ organizationId }) => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  transport: {
    read: {
      url: `/devops/v1/organizations/${organizationId}/cluster/overview`,
      method: 'get',
    },
  },
  fields: [

  ],
});
