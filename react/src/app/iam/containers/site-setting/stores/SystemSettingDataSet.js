export default ({ id = 0, hasRegister }) => {
  const fields = hasRegister ? [
    { name: 'registerEnabled', type: 'boolean', label: '是否启用注册' },
    {
      name: 'registerUrl',
      type: 'url',
      label: '注册页面链接',
      dynamicProps: ({ record }) => ({ required: record.get('registerEnabled') }),
    },
  ] : [];
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
          const {
            systemName,
            systemTitle,
            defaultLanguage,
            favicon,
            registerEnabled,
            autoCleanEmailRecord,
            autoCleanWebhookRecord,
          } = parseData || {};
          const dft = {
            systemName: systemName || 'Choerodon',
            systemTitle: systemTitle || 'Choerodon | 多云应用技术集成平台',
            defaultLanguage: defaultLanguage || 'zh_CN',
            favicon: favicon || '',
            registerEnabled: registerEnabled || false,
            autoCleanEmailRecord: autoCleanEmailRecord || false,
            autoCleanWebhookRecord: autoCleanWebhookRecord || false,
          };
          if (data === '{}') {
            return ({ new: true, ...dft });
          }
          return ({
            ...parseData,
            ...dft,
          });
        },
      },
      update: ({ data: [data] }) => {
        const postData = { ...data };
        if (!data.autoCleanEmailRecord && data.autoCleanEmailRecordInterval) {
          postData.autoCleanEmailRecordInterval = null;
        }
        if (!data.autoCleanWebhookRecord && data.autoCleanWebhookRecordInterval) {
          postData.autoCleanWebhookRecordInterval = null;
        }
        return ({
          url: '/iam/choerodon/v1/system/setting',
          method: 'post',
          data: postData,
        });
      },
    },
    fields: [
      {
        name: 'systemName', type: 'string', label: '平台简称', defaultValue: 'Choerodon', required: true,
      },
      { name: 'favicon', type: 'string', label: '平台logo' },
      { name: 'systemTitle', type: 'string', label: '平台全称' },
      { name: 'defaultLanguage', type: 'string', label: '平台默认语言' },
      { name: 'resetGitlabPasswordUrl', type: 'url', label: '重置gitlab密码页面链接' },
      { name: 'registerEnabled', type: 'boolean', label: '是否启用注册' },
      {
        name: 'registerUrl',
        type: 'url',
        label: '注册页面链接',
        dynamicProps: ({ record }) => ({ required: record.get('registerEnabled') }),
      },
      { name: 'systemLogo', type: 'string', label: '平台导航栏图形标' },
      { name: 'defaultPassword', type: 'string', label: '平台默认密码' },
      { name: 'themeColor', type: 'string', label: '系统主题色' },
      { name: 'autoCleanEmailRecord', type: 'boolean', label: '是否自动清理邮件日志' },
      { name: 'autoCleanWebhookRecord', type: 'boolean', label: '是否自动清理webhook日志' },
      {
        name: 'autoCleanEmailRecordInterval',
        type: 'number',
        label: '间隔',
        step: 1,
        min: 1,
        dynamicProps: ({ record }) => ({ required: record.get('emailEnabled') }),
      },
      {
        name: 'autoCleanWebhookRecordInterval',
        type: 'number',
        label: '间隔',
        step: 1,
        min: 1,
        dynamicProps: ({ record }) => ({ required: record.get('webhookEnabled') }),
      },
      ...fields,
    ],
  };
};
