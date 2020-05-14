
export default ({ organizationId }) => ({
  autoQuery: true,
  autoCreate: false,
  selection: false,
  paging: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/organizations/${organizationId}/appserver/overview?size=25`,
      method: 'get',
    },
  },
  fields: [

  ],
});
