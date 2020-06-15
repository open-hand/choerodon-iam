
export default ((id, orgId) => ({
  autoCreate: true,
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `iam/v1/${orgId}/ldaps/ldap-histories/${id}/error-users`,
      method: 'get',
    }),
  },
  queryFields: [
    { name: 'uuid', type: 'string', label: 'UUID' },
    { name: 'loginName', type: 'string', label: '登录名' },
    { name: 'realName', type: 'string', label: '用户名' },
    { name: 'email', type: 'string', label: '邮箱' },
    { name: 'cause', type: 'string', label: '失败原因' },
  ],
  fields: [
    { name: 'uuid', type: 'string', label: 'UUID' },
    { name: 'loginName', type: 'string', label: '登录名' },
    { name: 'realName', type: 'string', label: '用户名' },
    { name: 'email', type: 'string', label: '邮箱' },
    { name: 'cause', type: 'string', label: '失败原因' },
  ],
}));
