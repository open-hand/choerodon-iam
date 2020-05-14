export default () => ({
  selection: false,
  transport: {
    create: ({ data: [data] }) => ({
      url: '/iam/choerodon/v1/site/users/assign_roles',
      method: 'post',
      transformRequest: (() => {
        const rawData = data.memberId.map((v, index) => ({
          memberId: v,
          roleId: data.roleId[index],
        }));
        return JSON.stringify(rawData);
      }),
    }),
  },
  fields: [
    { name: 'memberId', label: '用户', textField: 'realName', valueField: 'id', required: true },
    { name: 'roleId', label: '角色', textField: 'name', valueField: 'id', required: true },
  ],
});
