export default (serviceOptionsDataSet, langOptionsDs) => ({
  autoQuery: true,
  selection: false,
  transport: {
    read: {
      url: '/iam/choerodon/v1/prompt',
      method: 'get',
    },
    update: ({ data: editData }) => ({
      url: `/iam/choerodon/v1/prompt/${editData[0].id}`,
      method: 'put',
      transformRequest: (([data]) => JSON.stringify(data)),
    }),
  },
  fields: [
    { name: 'promptCode', type: 'string', label: '编码', pattern: /^([a-zA-Z]|[0-9])([a-zA-Z0-9]|_|-|\.|\/)*/, required: true },
    { name: 'lang', type: 'string', label: '语言', required: true, textField: 'text', valueField: 'value', options: langOptionsDs },
    { name: 'description', type: 'string', label: '描述', required: true },
    { name: 'serviceCode', type: 'string', label: '所属微服务', required: true, textField: 'service', valueField: 'service', options: serviceOptionsDataSet },
  ],
  queryFields: [
    { name: 'promptCode', type: 'string', label: '编码' },
    { name: 'lang', type: 'string', label: '语言', textField: 'text', valueField: 'value', options: langOptionsDs },
    { name: 'description', type: 'string', label: '描述' },
    { name: 'serviceCode', type: 'string', label: '所属微服务', textField: 'service', valueField: 'service', options: serviceOptionsDataSet },
  ],
});
