import { axios } from '@choerodon/boot';
import { message } from 'choerodon-ui';

export default function (projectId, versionOptionDataSet) {
  async function checkName(value, name, record) {
    if (value === record.getPristineValue(name) || !value) return;
    if (!/^[a-zA-Z0-9.·\-_\s]+$/.test(value)) {
      return '只能由字母（大小写）、数字、"."、"·"、"_"、"-"、空格组成';
    }
    if (value.length > 30) {
      return '文本内容限制 30 字符，请重新输入';
    }
    try {
      const res = await axios.get(`/iam/choerodon/v1/projects/${projectId}/applications/${versionOptionDataSet.queryDataSet.current.get('applicationId')}/versions/check`, {
        params: {
          version: value,
        },
      });
      if (res.failed) {
        return res.message;
      }
      if (!res) {
        return '应用版本重复';
      }
    } catch (err) {
      return err;
    }
  }
  return {
    paging: false,
    dataKey: null,
    autoCreate: true,
    fields: [
      { name: 'versionName', type: 'string', label: '应用版本', required: true, validator: checkName },
    ],
  };
}
