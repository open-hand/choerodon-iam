import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

export default function (projectId, versionId, serviceTableDataSet, mobxStore) {
  const editorValidate = (value, name, record) => {
    // 匹配html界面为空白的正则。
    const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/g;
    if (!value || patternHTMLEmpty.test(value)) {
      return `请输入${record.getField(name).get('label')}`;
    }
    return true;
  };
  const descriptionValidator = (value) => {
    if (value && value.length > 250) {
      return '文本内容限制 250 字符，请重新输入';
    }
    return true;
  };
  const remarkValidator = (value) => {
    if (value && value.length > 250) {
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
      { name: 'imageUrl', type: 'string', label: '应用图标' },
      { name: 'version', type: 'string', label: '应用版本', required: true },
      { name: 'description', type: 'string', label: '应用描述', required: true, validator: descriptionValidator },
      { name: 'overview', type: 'string', label: '应用介绍' },
      { name: 'changelog', type: 'string', label: 'changelog' },
      { name: 'document', type: 'string', label: '文档' },
      { name: 'remark', type: 'string', label: '备注', validator: remarkValidator },
      { name: 'approveMessage', type: 'string', label: '驳回原因' },
    ],
    children: {
      containServices: serviceTableDataSet,
    },
    transport: {
      read: () => ({
        url: `iam/choerodon/v1/projects/${projectId}/publish_applications/versions/${versionId}/detail`,
        method: 'get',
      }),
      update: ({ data, dataSet }) => ({
        url: dataSet.submitUrl,
        method: 'put',
        data: {
          ...data[0],
          containServices: undefined,
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
