
export default ({ id = 0, organizationId, intl, intlPrefix }) => {
  const username = intl.formatMessage({ id: 'username' });
  const loginName = intl.formatMessage({ id: 'loginname' });
  const status = intl.formatMessage({ id: `${intlPrefix}.status` });
  const safeStatus = intl.formatMessage({ id: `${intlPrefix}.safe-status` });

  return {
    autoQuery: false,
    autoCreate: false,
    selection: false,
    fields: [
      { name: 'userName', label: username, textField: 'realName', valueField: 'id', required: true },
    ],
    transport: {
      create: ({ data, dataSet }) => ({
        url: '/base/v1/users/admin',
        method: 'post',
        data: undefined,
        params: {
          id: data[0].userName.join(','),
        },
      }),
    },
  };
};
