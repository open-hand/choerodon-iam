import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

export default function (projectId, appId, versionId, serviceTableDataSet, mobxStore) {
  const editorValidate = (value, name, record) => {
    // 匹配html界面为空白的正则。
    const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/g;
    if (!value || patternHTMLEmpty.test(value)) {
      return `请输入${record.getField(name).get('label')}`;
    }
    return true;
  };
  const validateDescription = (value) => {
    if (value.length > 250) {
      return '文本内容限制 250 字符，请重新输入';
    }
    return true;
  };

  return {
    autoQuery: true,
    autoCreate: true,
    paging: false,
    dataKey: null,
    fields: [
      //     appServiceDetailsVOS: [{id: 641, name: "websocketapp", code: "websocketapp", type: "normal",…}]
      // categoryName: "敏捷管理"
      // changelog: null
      // document: null
      // id: 8
      // latestVersionId: 78
      // name: "SVC测试应用num1"
      // objectVersionNumber: 27
      // overview: null
      // version: "1.112345"
      { name: 'id', type: 'string', ignore: 'always' },
      { name: 'latestVersionId', type: 'string', ignore: 'always' },
      { name: 'name', type: 'string', label: '应用名称', ignore: 'always' },
      { name: 'appServiceDetailsVOS', ignore: 'always' },
      { name: 'version', type: 'string', label: '应用版本', ignore: 'always' },
      { name: 'categoryName', type: 'string', label: '应用类型', ignore: 'always' },
      { name: 'description', type: 'string', label: '应用描述', required: true, validator: validateDescription },
      { name: 'overview', type: 'string', label: '应用介绍', validator: editorValidate },
      { name: 'changelog', type: 'string', label: 'changelog', required: true },
      { name: 'document', type: 'string', label: '文档', validator: editorValidate },
    ],
    children: {
      appServiceDetailsVOS: serviceTableDataSet,
    },
    transport: {
      read: () => ({
        url: `iam/choerodon/v1/projects/${projectId}/publish_applications/${appId}/versions/${versionId}/confirm`,
        method: 'get',
      }),
      update: ({ data, dataSet }) => ({
        url: dataSet.submitUrl,
        method: 'put',
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
