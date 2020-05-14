export default function (projectId) {
  return {
    autoQuery: true,
    paging: false,
    transport: {
      read: {
        url: `iam/choerodon/v1/projects/${projectId}/publish_applications/app_categories/list/enable`,
        method: 'get',
        transformResponse: (data) => [...JSON.parse(data).list.map((item) => ({
          ...item,
          type: item.name,
        })), {
          name: '新建应用类型',
          type: 'custom',
        }],
      },
    },
    fields: [
      { name: 'name', type: 'string' },
      { name: 'type', type: 'string' },
    ],
  };
}
