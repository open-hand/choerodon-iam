export default (langOptionDs, serviceOptionsDataSet) => ({
  autoquery: true,
  selection: false,
  transport: {
    create: {
      url: '/base/v1/prompt',
      method: 'post',
      transformRequest: (([data]) => JSON.stringify(data)),
    },
  },
  fields: [
    { name: 'promptCode', type: 'string', label: '编码', required: true, pattern: /^([a-zA-Z]|[0-9])([a-zA-Z0-9]|_|-|\.|\/)*/ },
    { name: 'lang', type: 'string', label: '语言', required: true, textField: 'text', valueField: 'value', options: langOptionDs },
    { name: 'serviceCode', type: 'string', label: '所属微服务', required: true, textField: 'service', valueField: 'service', options: serviceOptionsDataSet },
    { name: 'description', type: 'string', label: '描述', required: true },
  ],
});
