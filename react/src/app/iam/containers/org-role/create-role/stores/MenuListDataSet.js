export default ({ level, organizationId }) => ({
  autoQuery: false,
  selection: false,
  paging: false,
  idField: 'id',
  parentField: 'parentId',
  checkField: 'isChecked',
  expandField: 'expand',
  transport: {
    read: {
      url: `iam/choerodon/v1/organizations/${organizationId}/menus/${level}/permission-set-tree?tenant_id=${organizationId}`,
      method: 'get',
    },
  },
  fields: [
    { name: 'isChecked', type: 'boolean' },
    { name: 'name', type: 'string', label: '名称' },
    { name: 'route', type: 'string', label: '页面入口' },
  ],
});
