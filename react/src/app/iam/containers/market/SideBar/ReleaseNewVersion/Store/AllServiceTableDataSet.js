import { List } from 'choerodon-ui';

export default function (projectId, appId) {
  return {
    autoQuery: true,
    autoCreate: true,
    paging: false,
    selection: 'multiple',
    transport: {
      read: () => ({
        url: `/iam/choerodon/v1/projects/${projectId}/applications/${appId}/services`,
        method: 'get',
        transformResponse(data) {
          return JSON.parse(data).list.map((item) => ({
            ...item,
            selectedVersion: item.allAppServiceVersions instanceof Array ? item.allAppServiceVersions[0] : {},
          }));
        },
      }),
    },
    fields: [
      { name: 'id', type: 'string' },
      { name: 'code', type: 'string' },
      { name: 'name', type: 'string', label: '应用服务' },
      { name: 'type', type: 'string' },
      { name: 'appServiceVersions', type: 'object', label: '应用服务版本' },
      { name: 'allAppServiceVersions', type: 'object', label: '应用服务版本' },
    ],
    events: {
      load: ({ dataSet }) => {
        dataSet.forEach((item) => {
          if (!item.get('allAppServiceVersions')) {
            item.selectable = false;
          } else {
            dataSet.select(item);
          }
        });
      },
    },
  };
}
