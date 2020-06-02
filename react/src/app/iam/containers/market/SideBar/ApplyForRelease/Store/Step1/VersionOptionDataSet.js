import { axios } from '@choerodon/boot';

export default function (projectId, appOptionDataSet) {
  return {
    paging: false,
    dataKey: null,
    queryFields: [
      { name: 'refAppId', type: 'object', label: '需要发布的应用', textField: 'name', valueField: 'id', options: appOptionDataSet, required: true },
      { name: 'applicationName', type: 'string', bind: 'refAppId.name' },
      { name: 'applicationId', type: 'string', bind: 'refAppId.id' },
      { name: 'applicationDescription', type: 'string', bind: 'refAppId.description' },
    ],
    fields: [
      { name: 'id', type: 'number' },
      { name: 'version', type: 'string' },
      { name: 'description', type: 'string', label: '应用描述' },
    ],
    transport: {
      read: ({ data: { refAppId } }) => ({
        url: `iam/choerodon/v1/projects/${projectId}/applications/${refAppId.id}/versions/brief_info`,
        method: 'get',
        data: {},
      }),
    },
  };
}
