export default {
  // const username = intl.formatMessage({ id: 'username' });
  // const loginName = intl.formatMessage({ id: 'loginname' });
  autoQuery: false,
  selection: 'single',
  paging: false,
  transport: {
    read: (props) => ({
      url: '/iam/choerodon/v1/all/users',
      method: 'get',
      params: {
        organization_id: '0',
        param: '',
        sort: 'id',
        size: 0,
      },
    }),
  },
  fields: [
    { name: 'realName', type: 'string' },
    { name: 'loginName', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
};
