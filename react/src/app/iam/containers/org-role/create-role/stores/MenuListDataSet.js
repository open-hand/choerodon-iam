function getNode(node, res, name = 'subMenus') {
  res.push(node);
  if (node[name]) {
    node[name].forEach((n) => {
      getNode(n, res, name);
    });
  }
}

function getNodesByTree(tree, res, name = 'subMenus') {
  tree.forEach((node) => {
    getNode(node, res, name);
  });
}

export default ({ level, organizationId }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  idField: 'id',
  parentField: 'parentId',
  checkField: 'isChecked',
  expandField: 'expand',
  transport: {
    read: {
      url: `iam/choerodon/v1/organizations/${organizationId}/menus/organization/permission-set-tree?tenant_id=${organizationId}&menu_level=${level}`,
      method: 'get',
      transformResponse(data) {
        try {
          if (data && data.failed) {
            return data;
          }
          const arr = JSON.parse(data);
          const roleArray = [];
          getNodesByTree(arr, roleArray, 'subMenus');
          return {
            list: roleArray,
          };
        } catch (e) {
          return data;
        }
      },
    },
  },
  fields: [
    { name: 'isChecked', type: 'boolean' },
    { name: 'name', type: 'string', label: '名称' },
    { name: 'route', type: 'string', label: '页面入口' },
  ],
});
