export default (id, organizationId) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  filterBar: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/paas_app_market/${id}/versions?organization_id=${organizationId}`,
      method: 'get',
    },
  },
  fields: [
    { name: 'id', type: 'number' },
    { name: 'version', type: 'string' },
    { name: 'document', type: 'string' },
    { name: 'changelog', type: 'string' },
    { name: 'publishDate', type: 'string' },
    { name: 'versionCreationDate', type: 'string' },
    { name: 'downloadStatus', type: 'string' },
    { name: 'purchased', type: 'string' },
  ],
});
