import { axios } from '@choerodon/boot';

const intlPrefix = 'project.app-dataset';

export default function (intl, projectId) {
  return {
    autoQuery: false,
    paging: false,
    dataKey: null,
    fields: [
      { name: 'configurationValid', type: 'boolean' },
      { name: 'tokenValid', type: 'boolean' },
      { name: 'publishingPermissionValid', type: 'boolean' },
      { name: 'updateSuccessFlag', type: 'boolean' },
    ],
    transport: {
      read: {
        url: `iam/choerodon/v1/projects/${projectId}/publish_applications/verifyPermissions`,
        method: 'get',
      },
    },
  };
}
