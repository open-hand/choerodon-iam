export default (id, organizationId, autoQuery) => ({
  autoQuery: false,
  selection: false,
  paging: true,
  filterBar: false,
  pageSize: 5,
  transport: {
    read: {
      url: `/iam/choerodon/v1/paas_app_market/version/${id}?organization_id=${organizationId}`,
      method: 'get',
    },
  },
  fields: [
    { name: 'id', type: 'string' },
    { name: 'version', type: 'string' },
    { name: 'document', type: 'string' },
    { name: 'changelog', type: 'string' },
    { name: 'publishDate', type: 'string' },
    { name: 'versionCreationDate', type: 'string' },
    { name: 'downloadStatus', type: 'string' },
    { name: 'purchased', type: 'string' },
    { name: 'displayStatus', type: 'string' },
    { name: 'newVersion', type: 'boolean' },
    { name: 'marketAppServiceVOS', type: 'object' },
  ],
});
