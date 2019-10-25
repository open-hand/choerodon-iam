import { axios } from '@choerodon/boot';
import { omit } from 'lodash';
import { DataSet } from 'choerodon-ui/pro';

const intlPrefix = 'project.app-dataset';

const publishTypeOptionDataSet = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'mkt_deploy_only', value: '部署包' },
    { key: 'mkt_code_only', value: '源代码' },
    { key: 'mkt_code_deploy', value: '源代码、部署包' },
  ],
});

const freeOptionDataSet = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: true, value: '是' },
    { key: false, value: '否' },
  ],
});

const statusOptionDataSet = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'unpublished', value: '未发布' },
    { key: 'publishing', value: '发布中' },
    { key: 'published', value: '已发布' },
  ],
});

export default function (intl, projectId) {
  return {
    autoQuery: false,
    selection: false,
    queryFields: [
      { name: 'name', type: 'string', label: '应用名称' },
      { name: 'version', type: 'string', label: '最新版本' },
      { name: 'free', type: 'string', label: '是否免费', textField: 'value', valueField: 'key', options: freeOptionDataSet },
      { name: 'publish_type', type: 'string', label: '发布类型', textField: 'value', valueField: 'key', options: publishTypeOptionDataSet },
      { name: 'status', type: 'string', label: '应用状态', textField: 'value', valueField: 'key', options: statusOptionDataSet },
      { name: 'source_app_name', type: 'string', label: '应用来源' },
      { name: 'description', type: 'string', label: '应用描述' },
    ],
    fields: [
      { name: 'id', type: 'string' },
      { name: 'refAppId', type: 'string' },
      { name: 'appEditable', type: 'boolean' },
      { name: 'name', type: 'string', label: '应用名称' },
      { name: 'latestVersion', type: 'string', label: '最新版本' },
      { name: 'free', type: 'boolean', label: '是否免费' },
      { name: 'publishType', type: 'string', label: '发布类型' },
      { name: 'status', type: 'string', label: '应用状态' },
      { name: 'sourceAppName', type: 'string', label: '应用来源' },
      { name: 'description', type: 'string', label: '应用描述' },
    ],
    transport: {
      read: ({ data, params }) => ({
        url: `base/v1/projects/${projectId}/publish_applications`,
        method: 'get',
        params: {
          ...params,
          params: data.params ? data.params : undefined,
        },
        data: omit(data, ['params']),
      }),
    },
  };
}
