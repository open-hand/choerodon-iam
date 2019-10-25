
export default (id => ({
  autoCreate: true,
  autoQuery: true,
  selection: false,
  transport: {
    read: () => ({
      url: `/base/v1/organizations/2/ldap_histories/${id}/error_users?__id=`,
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
