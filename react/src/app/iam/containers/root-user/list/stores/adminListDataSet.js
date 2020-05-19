export default ({ id = 0, intl, intlPrefix }) => {
  const username = intl.formatMessage({ id: 'username' });
  const loginName = intl.formatMessage({ id: 'loginname' });
  const status = intl.formatMessage({ id: `${intlPrefix}.status` });
  const safeStatus = intl.formatMessage({ id: `${intlPrefix}.safe-status` });
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: '/iam/choerodon/v1/users/admin',
        method: 'get',
        transformResponse: (data) => ({
          list: JSON.parse(data).content,
          ...JSON.parse(data),
        }),
      },
    },
    fields: [
      { name: 'realName', type: 'string', label: username },
      { name: 'loginName', type: 'string', label: loginName },
      { name: 'enabled', type: 'boolean', label: status },
      { name: 'locked', type: 'boolean', label: safeStatus },
      { name: 'creationDate', type: 'date', label: '创建日期' },
    ],
    queryFields: [
      { name: 'realName', type: 'string', label: username },
      { name: 'loginName', type: 'string', label: loginName },
    ],
  };
};
