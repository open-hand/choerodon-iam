export default ({ id = 0 }) => {
  function checkMinLength(value, name, record) {
    if (value > record.get('maxPasswordLength')) {
      return '最小密码长度必须小于最大密码长度';
    }
  }
  function checkMaxLength(value, name, record) {
    if (value < record.get('minPasswordLength')) {
      return '最大密码长度必须大于最小密码长度';
    }
  }
  return {
    // autoCreate: true,
    autoQuery: true,
    transport: {
      read: {
        url: '/iam/choerodon/v1/system/setting',
        method: 'get',
        dataKey: null,
        transformResponse: (data) => {
          const parseData = JSON.parse(data);
          const { defaultPassword, minPasswordLength, maxPasswordLength } = parseData;
          const dft = {
            defaultPassword: defaultPassword || 'abcd1234',
            minPasswordLength: minPasswordLength || 6,
            maxPasswordLength: maxPasswordLength || 18,
          };
          if (!defaultPassword && !minPasswordLength && !maxPasswordLength) {
            return ({ new: true, ...dft });
          } else {
            return ({
              ...parseData,
              ...dft,
            });
          }
        },
      },
      update: ({ data }) => ({
        url: '/iam/choerodon/v1/system/setting/passwordPolicy',
        method: 'post',
        data: data[0],
      }),
    },
    fields: [
      { name: 'defaultPassword', type: 'string', label: '平台默认密码', required: true },
      { name: 'minPasswordLength', type: 'number', min: 0, validator: checkMinLength, label: '平台默认最小密码长度', required: true },
      { name: 'maxPasswordLength', type: 'number', min: 0, max: 65535, validator: checkMaxLength, label: '平台默认最大密码长度', required: true },
    ],
  };
};
