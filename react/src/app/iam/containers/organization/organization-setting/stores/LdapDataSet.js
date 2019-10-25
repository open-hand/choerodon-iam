import { DataSet } from 'choerodon-ui/pro';

export default ({ orgId, name }) => {
  const directoryTypeOption = new DataSet({
    data: [{
      value: 'Microsoft Active Directory',
      meaning: 'Microsoft Active Directory',
    }, {
      value: 'OpenLDAP',
      meaning: 'OpenLDAP',
    }],
  });
  return {
    autoCreate: true,
    autoQuery: true,
    transport: {
      read: {
        url: `/base/v1/organizations/${orgId}/ldaps`,
        method: 'get',
        dataKey: null,
      },
      submit: ({ data: [ldap] }) => ({
        url: `/base/v1/organizations/${orgId}/ldaps/${ldap.id || ''}`,
        method: 'post',
        dataKey: null,
        data: ldap,
      }),
    },
    fields: [
      { name: 'name', type: 'string', defaultValue: name },
      { name: 'organizationId', type: 'string', defaultValue: orgId },
      { name: 'directoryType', type: 'string', label: '目录类型', required: true, options: directoryTypeOption },
      { name: 'serverAddress', type: 'string', label: '主机名', required: true }, // 必填
      { name: 'useSSL', type: 'boolean', label: '是否使用SSL', defaultValue: false },
      { name: 'port', type: 'number', label: '端口号', required: true }, // 必填
      { name: 'enabled', type: 'boolean' }, 
      { name: 'sagaBatchSize', type: 'number', label: '同步用户saga发送用户数量', min: 1, step: 1, defaultValue: 500, required: true }, // 必填
      { name: 'connectionTimeout', type: 'number', label: 'ldap服务器连接超时时间', min: 0, defaultValue: 10, required: true },
      { name: 'baseDn', type: 'string', label: '基准DN', help: 'LDAP目录树的最顶部的根，从根节点搜索用户。例如：cn=users,dc=example,dc=com' },
      { name: 'account', type: 'string', label: '管理员登录名', help: '用户登录到 LDAP。例如：user@domain.name 或 cn =用户, dc =域、dc =名称', required: true }, // 必填
      { name: 'password', type: 'string', label: '管理员密码', required: true }, // 必填
      { name: 'objectClass', type: 'string', label: '用户对象类', help: '支持多个objectclass，使用逗号分隔', required: true }, // 多个objectClass以逗号分割/非必填
      { name: 'loginNameField', type: 'string', label: '登录名属性', required: true },
      { name: 'emailField', type: 'string', label: '邮箱属性', required: true },
      { name: 'realNameField', type: 'string', label: '用户名属性' },
      { name: 'phoneField', type: 'string', label: '手机号属性' },
      { name: 'uuidField', type: 'string', label: 'uuid属性', help: 'ldap对象的唯一标识，大多数是\'entryUUID\'属性，Microsoft Active Directory可能是\'objectGUID\'属性，如果您的的ldap服务器确实不支持uuid，使用能唯一标识对象的字段即可，比如\'uid\'或者\'entryDN\'。', required: true },
      { name: 'customFilter', type: 'string', label: '自定义筛选用户条件', help: '额外的过滤条件用于同步用户，允许为空，表达式必须以\'(\'开始，以\')\'结束，语法参考ldap search syntax。' },
    ],
  };
};
