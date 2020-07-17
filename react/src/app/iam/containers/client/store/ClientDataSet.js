export default function (orgId, optionsDataSet) {
  function checkIsJson(value, name, record) {
    try {
      const obj = JSON.parse(value);
      if (typeof obj === 'object' && obj) {
        return true;
      } else {
        return '请输入JSON格式的附加信息';
      }
    } catch (e) {
      return '请输入JSON格式的附加信息';
    }
  }
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: `/iam/v1/${orgId}/clients`,
        method: 'get',
      },
      create: ({ data: [data] }) => ({
        url: `/iam/v1/${orgId}/clients`,
        method: 'post',
        data: { ...data, pwdReplayFlag: 0 },
      }),
      update: ({ data: [data] }) => ({
        url: `/iam/v1/${orgId}/clients`,
        method: 'put',
        data: { ...data, pwdReplayFlag: 0 },
      }),
    },
    fields: [
      { name: 'id', type: 'string', label: '客户端ID' },
      { name: 'name', type: 'string', label: '客户端ID', required: true },
      { name: 'authorizedGrantTypes', type: 'string', label: '授权类型', required: true, multiple: ',' },
      { name: 'secret', type: 'string', label: '密钥', required: true },
      { name: 'accessTokenValidity', type: 'number', label: '访问授权超时', min: 60, required: true },
      { name: 'refreshTokenValidity', type: 'number', label: '授权超时', min: 60, required: true },
      { name: 'scope', type: 'string', label: '作用域', multiple: ',', help: '作用域为申请的授权范围。您最多可输入6个域。' },
      { name: 'autoApprove', type: 'string', label: '自动授权域', multiple: ',', help: '自动授权域为oauth认证后，系统自动授权而非用户手动添加的作用域。您最多可输入6个域。' },
      { name: 'webServerRedirectUri', type: 'string', label: '重定向地址' },
      { name: 'additionalInformation', type: 'string', label: '附加信息', validator: checkIsJson },
      { name: 'roles', type: 'string', label: '角色名称', textField: 'name', valueField: 'id' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: '客户端ID' },
    ],
    events: {
      update: ({ record, name, value }) => {
        if (name === 'accessTokenValidity' || name === 'refreshTokenValidity') {
          if (value < 60) {
            record.set(name, 60);
          }
        }
      },
    },
  };
}
