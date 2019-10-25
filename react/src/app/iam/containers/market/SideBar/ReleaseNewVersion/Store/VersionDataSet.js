import { axios } from '@choerodon/boot';
import { DataSet, message } from 'choerodon-ui/pro';
import { version } from 'choerodon-ui';

export default function (projectId, appId, queryUrl) {
  const editorValidate = async (value, name, record) => {
    // 匹配html界面为空白的正则。
    const patternHTMLEmpty = /^(((<[^i>]+>)*\s*)|&nbsp;|\s)*$/g;
    if (!value || patternHTMLEmpty.test(value)) {
      return `请输入${record.getField(name).get('label')}`;
    }
    return true;
  };

  const emailValidator = (value) => {
    if (!/^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/.test(value)) {
      return '邮箱不符合规范';
    }
    return true;
  };

  const validVersion = async (value) => {
    if (!/^[a-zA-Z0-9_.-]+$/.test(value)) {
      return '只能由大小写字母、数字、"."、"_"、"-"、组成';
    }
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    try {
      const res = await axios.get(`/base/v1/projects/${projectId}/applications/${appId}/versions/check?version=${value}`);
      if (res.failed) {
        return res.message;
      }
      if (!res) {
        return '版本已存在';
      }
    } catch (err) {
      return err;
    }
    return true;
  };

  return {
    autoQuery: true,
    paging: false,
    dataKey: null,
    submitUrl: queryUrl,
    fields: [
      { name: 'name', type: 'string', label: '应用名称', ignore: 'always', required: true },
      { name: 'whetherToCreate', type: 'boolean', required: true, label: '选择应用版本', defaultValue: false },
      { name: 'version', type: 'string', label: '应用版本', required: true, validator: validVersion },
      { name: 'notificationEmail', type: 'string', label: '通知邮箱', required: true, validator: emailValidator },
      { name: 'document', type: 'string', label: '文档', required: true, validator: editorValidate },
      { name: 'changelog', type: 'string', label: 'changelog', required: true },
      { name: 'remark', type: 'string', label: '备注' },
    ],
    transport: {
      read: () => ({
        url: queryUrl,
        method: 'get',
        transformResponse(data) {
          return ({
            ...JSON.parse(data),
            whetherToCreate: !!data.whetherToCreate,
          });
        },
      }),
    },
  };
}
