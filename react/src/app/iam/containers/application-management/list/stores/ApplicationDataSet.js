import { axios, Choerodon } from '@choerodon/boot';

const regName = /^[\u4e00-\u9fa5._\-a-zA-Z0-9\s]{1,32}$/;
export default ({ id = 0, intl, intlPrefix }) => {
  const name = intl.formatMessage({ id: 'name' });
  const description = intl.formatMessage({ id: 'description' });
  async function checkName(value, fieldName, record) {
    if (value === record.getPristineValue(fieldName) || !value) return;
    if (!regName.test(value)) {
      return '应用名称只能由汉字、字母、数字、"_"、"."、"-"、"——"和空格组成。';
    }
    try {
      const result = await axios.get(`/iam/choerodon/v1/projects/${id}/applications/check/${value}`);
      if (!result) {
        return '该名称已被使用。';
      }
    } catch (e) {
      Choerodon.prompt(e);
    }
  }

  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: `/iam/choerodon/v1/projects/${id}/applications/pagingByOptions?type=custom`,
        method: 'get',
      },
      create: ({ data: [data] }) => ({
        url: `/iam/choerodon/v1/projects/${id}/applications`,
        method: 'post',
        data,
      }),
      update: ({ data: [data] }) => ({
        url: `/iam/choerodon/v1/projects/${id}/applications/${data.id}`,
        method: 'put',
        data,
      }),
    },
    fields: [
      { name: 'name', type: 'string', label: name, required: true, validator: checkName, maxLength: 32 },
      { name: 'description', type: 'string', label: description },
      { name: 'creatorRealName', type: 'string', label: '创建人' },
      { name: 'creationDate', type: 'datetime', label: '创建时间' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: name },
      { name: 'description', type: 'string', label: description },
    ],
  };
};
