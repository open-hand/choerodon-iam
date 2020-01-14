export default ({ id = 0, hasRegister }) => {
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
  const fields = hasRegister ? [
    { name: 'registerEnabled', type: 'boolean', label: '是否启用注册' },
    { name: 'registerUrl',
      type: 'url',
      label: '注册页面链接',
      dynamicProps: ({ record }) => ({ required: record.get('registerEnabled') }) },
  ] : [];
  return {
    // autoCreate: true,
    autoQuery: true,
    transport: {
      read: {
        url: '/base/v1/system/setting',
        method: 'get',
        dataKey: null,
        transformResponse: (data) => {
          const parseData = JSON.parse(data);
          const dft = {
            systemName: parseData.systemName || 'Choerodon',
            systemTitle: parseData.systemTitle || 'Choerodon | 多云应用技术集成平台',
            defaultLanguage: parseData.defaultLanguage || 'zh_CN',
            favicon: parseData.favicon || '',
            registerEnabled: parseData.registerEnabled || false,
          };
          if (data === '{}') {
            return ({ new: true, ...dft });
          } else {
            return ({
              ...parseData,
              ...dft,
            });
          }
        },
      },
      update: ({ data, dataSet }) => ({
        url: '/base/v1/system/setting',
        method: 'post',
        data: data[0],
      }),
    },
    fields: [
      { name: 'systemName', type: 'string', label: '平台简称', defaultValue: 'Choerodon', required: true },
      { name: 'favicon', type: 'string', label: '平台logo' },
      { name: 'systemTitle', type: 'string', label: '平台全称' },
      { name: 'defaultLanguage', type: 'string', label: '平台默认语言' },
      { name: 'resetGitlabPasswordUrl', type: 'url', label: '重置gitlab密码页面链接' },
      { name: 'registerEnabled', type: 'boolean', label: '是否启用注册' },
      { name: 'registerUrl',
        type: 'url',
        label: '注册页面链接',
        dynamicProps: ({ record }) => ({ required: record.get('registerEnabled') }) },
      { name: 'systemLogo', type: 'string', label: '平台导航栏图形标' },
      { name: 'defaultPassword', type: 'string', label: '平台默认密码' },
      { name: 'themeColor', type: 'string', label: '系统主题色' },
      ...fields,
    ],
  };
};
