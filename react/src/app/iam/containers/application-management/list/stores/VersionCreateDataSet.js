import { axios, Choerodon } from '@choerodon/boot';

const regVersionName = new RegExp('^[.·_\\-a-zA-Z0-9\\s]{1,30}$');
export default ({ id = 0, intl }) => {
  async function check(value, name, record) {
    if (value === record.getPristineValue(name) || !value) return;
    if (!regVersionName.test(value)) {
      return '版本名称只能由大小写字母、数字、“.”、“·”、“-”、“_”、空格组成。';
    }
    const applicationId = record.get('applicationId');
    try {
      const result = await axios.get(`/base/v1/projects/${id}/applications/${applicationId}/versions/check?version=${value}`);
      if (result) {
        return;
      } else {
        return '版本名称已存在。';
      }
    } catch (e) {
      Choerodon.prompt(e);
    }
  }
  return {
    autoQuery: false,
    selection: false,
    transport: {
      read: ({ data }) => ({
        url: `/base/v1/projects/${id}/applications/${data.applicationId}/versions/${data.versionId}`,
        method: 'get',
      }),
      create: ({ data: [data] }) => ({
        url: `/base/v1/projects/${id}/applications/${data.applicationId}/versions`,
        method: 'post',
        data,
      }),
      update: ({ data: [data] }) => ({
        url: `/base/v1/projects/${id}/applications/${data.applicationId}/versions/${data.id}`,
        method: 'put',
        data,
      }),
    },
    fields: [
      { name: 'version', type: 'string', label: '版本名称', required: true, validator: check },
      { name: 'description', type: 'string', label: '版本说明' },
    ],
  };
};
