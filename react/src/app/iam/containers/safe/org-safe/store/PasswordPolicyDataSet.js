export default function passwordPoliciesDataSet(organizationId, id, intl, intlPrefix) {
  function getAllCount(record) {
    const digitsCount = record.get('digitsCount');
    const specialCharCount = record.get('specialCharCount');
    const lowercaseCount = record.get('lowercaseCount');
    const uppercaseCount = record.get('uppercaseCount');

    return digitsCount + specialCharCount + lowercaseCount + uppercaseCount;
  }
  function checkMinLength(value, name, record) {
    if (value > record.get('maxLength')) {
      return intl.formatMessage({ id: `${intlPrefix}.min.lessthan.more` });
    }
  }
  function checkMaxLength(value, name, record) {
    if (getAllCount(record) > value) {
      return intl.formatMessage({ id: `${intlPrefix}.max.length` });
    }
  }
  const fields = [
    { name: 'enablePassword', type: 'boolean', label: '是否启用' },
    { name: 'notUsername', type: 'boolean', label: '是否允许密码与登录名相同' },
    { name: 'originalPassword', type: 'string', label: '新用户默认密码' },
    { name: 'minLength', type: 'number', step: 1, min: 0, max: 65535, label: '最小密码长度', validator: checkMinLength, defaultValue: 0 },
    { name: 'maxLength', type: 'number', step: 1, min: 0, max: 65535, label: '最大密码长度', validator: checkMaxLength, defaultValue: 0 },

    { name: 'digitsCount', type: 'number', step: 1, min: 0, max: 65535, label: '最少数字数', defaultValue: 0 },
    { name: 'specialCharCount', type: 'number', step: 1, min: 0, max: 65535, label: '最少特殊字符', defaultValue: 0 },
    { name: 'lowercaseCount', type: 'number', step: 1, min: 0, max: 65535, label: '最少小写字母数', defaultValue: 0 },
    { name: 'uppercaseCount', type: 'number', step: 1, min: 0, max: 65535, label: '最少大写字母数', defaultValue: 0 },

    { name: 'notRecentCount', type: 'number', step: 1, min: 0, max: 65535, label: '最大近期密码', defaultValue: 0 },
    { name: 'regularExpression', type: 'string', label: '密码正则' },
    { name: 'enableSecurity', type: 'boolean', label: '是否启用' },
    { name: 'enableCaptcha', type: 'boolean', label: '是否开启验证码' },
    { name: 'maxCheckCaptcha', type: 'number', step: 1, min: 0, max: 65535, label: '输错次数', defaultValue: 0 },
    { name: 'enableLock', type: 'boolean', label: '是否开启锁定' },
    { name: 'maxErrorTime', type: 'number', step: 1, min: 0, max: 65535, label: '输错次数', defaultValue: 0 },
    { name: 'lockedExpireTime', type: 'number', min: 0, label: '锁定时长', defaultValue: 0 },
  ];

  return {
    autoQuery: true,
    dataKey: null,
    paging: false,
    transport: {
      read: {
        url: `/iam/v1/${organizationId}/password-policies`,
        method: 'get',
      },
      submit: (record) => {
        if (record.data[0].id) {
          return {
            url: `/iam/v1/${organizationId}/password_policies/${record.data[0].id}`,
            method: 'post',
            transformRequest: (([data]) => {
              Object.keys(data).forEach((key) => {
                const field = fields.find((v) => v.name === key);
                if ((data[key] === null || data[key] === undefined) && field && field.type === 'number') {
                  data[key] = 0;
                }
              });
              fields.forEach((v) => {
                if (v.type === 'number' && data[v.name] === undefined) {
                  data[v.name] = 0;
                } else if (data[v.name] === undefined || data[v.name] === null) {
                  data[v.name] = '';
                }
              });
              return JSON.stringify(data);
            }),
          };
        }
      },
      fields,
    },
  };
}
