import querystring from 'query-string';

export default (intl, intlPrefix) => ({
  autoQuery: true,
  selection: false,
  fields: [
    { name: 'name', type: 'string', label: '类型名称' },
    { name: 'code', type: 'string', label: '类型编码' },
    { name: 'description', type: 'string', label: '类型说明' },
    { name: 'builtInFlag', type: 'boolean', label: '来源' },
  ],
  transport: {
    read: {
      url: '/iam/choerodon/v1/categories/org',
      method: 'get',
      paramsSerializer: (params) => querystring.stringify(params, { arrayFormat: 'brackets' }),
    },

  },
});
