export default ({ id = 0 }) => ({
  // autoCreate: true,
  autoQuery: true,
  transport: {
    read: {
      url: `/iam/choerodon/v1/organizations/${id}/org_level`,
      method: 'get',
      transformResponse: ((data, headers) => {
        const newData = JSON.parse(data);
        newData.tenantConfigVO = {
          ...newData.tenantConfigVO,
          name: newData.tenantName,
          code: newData.tenantNum,
        };
        return ({
          list: [newData.tenantConfigVO],
        });
      }),
    },
    update: {
      url: `/iam/choerodon/v1/organizations/${id}/organization_level`,
      method: 'put',
      transformRequest: (([data], headers) => {
        if (!data.homePage) {
          data.homePage = '';
        }
        return JSON.stringify(data);
      }),
      transformResponse: ((data, headers) => ({
        list: [JSON.parse(data)],
      })),
    },
  },
  fields: [
    { name: 'imageUrl', type: 'string', label: '组织Logo' },
    { name: 'name', type: 'string', label: '组织名称', defaultValue: '汉得', required: true },
    { name: 'code', type: 'string', label: '组织编码', required: true },
    { name: 'address', type: 'string', label: '组织所在地' },
    { name: 'homePage', type: 'url', label: '官网地址', defaultValue: '无' },
    { name: 'ownerRealName', type: 'string', label: '所有者' },
  ],
});
