import { List } from 'choerodon-ui';

export default function (projectId, appId, selectVersionsDataSet) {
  return {
    autoQuery: false,
    // autoCreate: true,
    dataKey: null,
    paging: false,
    selection: false,
    transport: {
      read: ({ data: { versionId } }) => ({
        url: `/base/v1/projects/${projectId}/applications/${appId}/versions/${versionId}/svc_versions`,
        method: 'get',
        data: {},
        // transformResponse: (data) => JSON.parse(data).map((item) => ({ ...item, appServiceMarketVersion: item.appServiceVersionUploadPayloads && item.appServiceVersionUploadPayloads.length ? item.appServiceVersionUploadPayloads[0] : {} })),
      }),
    },
    queryFields: [
      { name: 'version', label: '应用版本', type: 'object', textField: 'version', valueField: 'id', options: selectVersionsDataSet, required: true },
      { name: 'versionId', type: 'string', bind: 'version.id' },
    ],
    fields: [
      { name: 'id', type: 'number' },
      { name: 'code', type: 'string' },
      { name: 'name', type: 'string', label: '应用服务' },
      { name: 'type', type: 'string' },
      { name: 'appServiceVersions', type: 'object', label: '应用服务版本' },
      { name: 'allAppServiceVersions', type: 'object' },
    ],
    // events: {
    //   load: ({ dataSet }) => {
    //     dataSet.forEach((item) => {
    //       if (!item.get('appServiceVersionUploadPayloads').length) {
    //         item.selectable = false;
    //       } else {
    //         dataSet.select(item);
    //       }
    //     });
    //   },
    // },
  };
}
