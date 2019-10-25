
export default ({ id = 0, intl, intlPrefix, applicationId }) => {
  const name = intl.formatMessage({ id: 'name' });
  const description = intl.formatMessage({ id: 'description' });
  
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: `/base/v1/projects/${id}/applications/${applicationId}/services/paging/app`,
        method: 'get',
      },
      create: ({ data: [data] }) => ({
        url: `/base/v1/projects/${id}/applications/${data.id}/services?service_ids=${data.serviceIds.join(',')}`,
        method: 'post',
        data,
      }),
    },
    fields: [
      { name: 'name', type: 'string', label: '名称' },
      { name: 'code', type: 'string', label: '编码' },
      { name: 'type', type: 'string', label: '类型' },
      { name: 'status', type: 'string', label: '状态' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: name },
    ],
  };
};
