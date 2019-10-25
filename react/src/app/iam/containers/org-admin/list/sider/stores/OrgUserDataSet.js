export default ({ id = 0, intl, intlPrefix }) => {
  const username = intl.formatMessage({ id: 'username' });
  const loginName = intl.formatMessage({ id: 'loginname' });
  return {
    autoQuery: false,
    selection: 'single',
    paging: false,
    transport: {
      read: () => ({
        url: `/base/v1/organizations/${id}/enableUsers`,
        method: 'get',
        params: {
          user_name: '',
        },
      }),
    },
    fields: [
      { name: 'realName', type: 'string', label: username },
      { name: 'loginName', type: 'string', label: loginName },
      { name: 'id', type: 'number', unique: true },
    ],
  };
};
