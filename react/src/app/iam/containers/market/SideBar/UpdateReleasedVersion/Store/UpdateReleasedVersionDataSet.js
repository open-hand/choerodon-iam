import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

export default function (projectId, organizationId, appId, versionId, serviceTableDataSet, mobxStore) {
  const editorValidate = (value, name, record) => {
    // 匹配html界面为空白的正则。
    const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/g;
    if (!value || patternHTMLEmpty.test(value)) {
      return `请输入${record.getField(name).get('label')}`;
    }
    return true;
  };

  return {
    autoQuery: true,
    autoCreate: true,
    paging: false,
    dataKey: null,
    fields: [
      { name: 'version', type: 'string', label: '应用版本' },
      { name: 'whetherToFix', type: 'boolean', label: '更新修复版本' },
      { name: 'disableWhetherToFix', type: 'boolean' },
      { name: 'changelog', type: 'string', label: 'changelog', validator: editorValidate },
      { name: 'document', type: 'string', label: '文档', validator: editorValidate },
      { name: 'objectVersionNumber', type: 'number' },
    ],
    children: {
      appServiceDetailsVOS: serviceTableDataSet,
    },
    transport: {
      read: () => ({
        url: `base/v1/projects/${projectId}/publish_applications/${appId}/versions/${versionId}`,
        method: 'get',
        transformResponse: (data) => ({
          ...JSON.parse(data),
          disableWhetherToFix: !JSON.parse(data).whetherToFix,
        }),
      }),
      update: ({ data, dataSet }) => ({
        url: `base/v1/projects/${projectId}/publish_applications/${appId}/versions/${versionId}`,
        method: 'put',
        params: {
          organization_id: organizationId,
        },
        data: {
          ...data[0],
          appServiceDetailsVOS: undefined,
        },
      }),
    },
    events: {
      load: ({ dataSet }) => {
        dataSet.forEach((item) => {
          item.status = 'update';
          mobxStore.setOverview(item.get('overview') || '');
          mobxStore.setDocument(item.get('document') || '');
        });
      },
    },
  };
}
