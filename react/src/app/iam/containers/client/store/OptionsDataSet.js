export default function (orgId) {
  return {
    selection: 'multiple',
    queryUrl: `/base/v1/organizations/${orgId}/role_members/clients/count`,
    autoQuery: true,
  };  
}
