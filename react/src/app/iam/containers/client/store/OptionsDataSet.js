export default function (orgId) {
  return {
    selection: 'multiple',
    queryUrl: `/iam/choerodon/v1/organizations/${orgId}/role_members/clients/count`,
    autoQuery: true,
  };
}
