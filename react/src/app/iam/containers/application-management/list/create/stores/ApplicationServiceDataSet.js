
export default ({ id = 0, intl, intlPrefix, type }) => {
  const name = intl.formatMessage({ id: 'name' });
  const description = intl.formatMessage({ id: 'description' });

  return {
    autoQuery: true,
    // selection: false,
    cacheSelection: true,
    transport: {
      read: {
        url: `/iam/choerodon/v1/projects/${id}/services/paging/${type}`,
        method: 'get',
      },
    },
    fields: [
      { name: 'id', unique: true },
      { name: 'name', type: 'string', label: '服务名称' },
      { name: 'code', type: 'string', label: '服务编码' },
      { name: 'type', type: 'string', label: '服务类型' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: name },
    ],
  };
};
