import { omit } from 'lodash';

export default function (intl, projectId, appId) {
  return {
    autoQuery: true,
    selection: false,
    paging: false,
    fields: [
      { name: 'id', type: 'string' },
      { name: 'refAppId', type: 'string' },
      { name: 'appEditable', type: 'boolean' },
      { name: 'version', type: 'string', label: '应用版本' },
      { name: 'publishErrorCode', type: 'string' },
      { name: 'status', type: 'string', label: '版本状态' },
      { name: 'sourceAppName', type: 'string', label: '应用服务' },
    ],
    transport: {
      read: ({ data, params }) => ({
        url: `iam/choerodon/v1/projects/${projectId}/publish_applications/versions`,
        method: 'get',
        params: {
          ...params,
          params: (data.params && data.params.length) ? data.params[0] : undefined,
          application_id: appId,
        },
        data: omit(data, ['params']),
      }),
    },
  };
}
