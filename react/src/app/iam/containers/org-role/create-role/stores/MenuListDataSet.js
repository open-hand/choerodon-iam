function getNode(node, res, name = 'subMenus') {
  node.expand = false;
  res.push(node);
  if (node[name]) {
    node[name].forEach((n) => {
      getNode(n, res, name = 'subMenus');
    });
  }
}

function getNodesByTree(tree, res, name = 'subMenus') {
  tree.forEach((node) => {
    getNode(node, res, name = 'subMenus');
  });
}

export default ({ level }) => ({
  autoQuery: true,
  selection: false,
  paging: false,
  idField: 'code',
  parentField: 'parentCode',
  checkField: 'isChecked',
  expandField: 'expand',
  transport: {
    read: {
      url: `/iam/choerodon/v1/menus/menu_config?code=choerodon.code.top.${level}`,
      method: 'get',
      transformResponse(data) {
        try {
          if (data && data.failed) {
            return data;
          }
          const arr = JSON.parse(data).subMenus;
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
