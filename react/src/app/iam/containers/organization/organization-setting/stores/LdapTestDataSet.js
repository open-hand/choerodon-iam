function handleData(data, dataSet) {
  const lists = JSON.parse(data);
  const { id } = dataSet.current;
  /*eslint-disable */
  lists.__id = id;
  return [lists];
}
function handleRequestData(data) {
  console.log('[Data]', data);
  const lists = data[0];
  return JSON.stringify(lists);
}
export default ({ orgId }) => ({
  transport: {
    create: ({ dataSet, data: [ldap] }) => ({
      url: `/base/v1/organizations/${orgId}/ldaps/${ldap.id}/test_connect`,
      method: 'post',
      transformRequest: ((data) => handleRequestData(data, dataSet)),
      transformResponse: ((data) => ({
        list: handleData(data, dataSet),
      })),
    }),
    update: ({ dataSet, data: [ldap] }) => ({
      url: `/base/v1/organizations/${orgId}/ldaps/${ldap.id}/test_connect`,
      method: 'post',
      transformRequest: ((data, headers) => handleRequestData(data, dataSet)),
      transformResponse: ((data, headers) => ({
        list: handleData(data, dataSet),
      })),
    }),
  },
  fields: [
    { name: 'account', type: 'string', label: '管理员登录名', defaultValue: '', required: true},
    { name: 'password', type: 'string', label: '管理员密码', defaultValue: '', required: true}, // 必填
    { name: 'canLogin', type: 'boolean', label: 'LDAP登录' },
    { name: 'canConnectServer', type: 'boolean', label: '基础链接' },
    { name: 'matchAttribute', type: 'boolean', label: '用户属性链接' },
  ],

});
