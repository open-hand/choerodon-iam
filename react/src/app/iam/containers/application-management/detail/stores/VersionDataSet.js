
export default ({ id = 0, intl, intlPrefix, applicationId }) => {
  const name = intl.formatMessage({ id: 'name' });
  const description = intl.formatMessage({ id: 'description' });
  
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: `/base/v1/projects/${id}/applications/${applicationId}/versions`,
        method: 'get',
      },
      create: ({ data: [data] }) => ({
        url: `/base/v1/projects/${id}/applications/${applicationId}/versions`,
        method: 'post',
        data,
      }),
    },
    fields: [
      { name: 'version', type: 'string', label: '版本名称' },
      { name: 'description', type: 'string', label: '版本说明' },
      { name: 'status', type: 'string', label: '状态' },
    ],
    queryFields: [
      { name: 'version', type: 'string', label: '版本名称' },
    ],
  };
};
