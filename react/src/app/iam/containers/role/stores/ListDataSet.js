/* eslint-disable no-nested-ternary */
import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

const buildInDs = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'strig' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'true', value: '预定义' },
    { key: 'false', value: '自定义' },
  ],
});

const enabledDs = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'strig' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'true', value: '启用' },
    { key: 'false', value: '停用' },
  ],
});

export default ({ level, gitlabLabelDs }) => {
  const codeValidator = async (value, name, record) => {
    const validValue = `role/${level}/custom/${value}`;
    if (record.status !== 'add') {
      return true;
    }
    if (!value) {
      return '编码必输。';
    }
    if (value.trim() === '') {
      return '编码不能全为空格。';
    }
    if (validValue.length > 64) {
      return '编码长度不能超过64！';
    } else if (value.trim() === '') {
      return '编码不能全为空！';
    }
    const reg = /^[a-z]([-a-z0-9]*[a-z0-9])?$/;
    if (!reg.test(value)) {
      return '编码只能由小写字母、数字、"-"组成，且以小写字母开头，不能以"-"结尾。';
    }
    if (record.status === 'add') {
      try {
        const params = { code: validValue };
        const res = await axios.post('/iam/choerodon/v1/roles/check', JSON.stringify(params));
        if (res.failed) {
          return '编码已存在。';
        } else {
          return true;
        }
      } catch (err) {
        return '编码重名校验失败，请稍后再试。';
      }
    } else {
      return true;
    }
  };
  const nameValidator = async (value, name, record) => {
    if (!value) {
      return '编码必输。';
    }
    if (value.trim() === '') {
      return '编码不能全为空格。';
    }
    return true;
  };

  return {
    autoQuery: true,
    transport: {
      read: ({ params, data }) => ({
        url: '/iam/choerodon/v1/roles/search',
        method: 'get',
        params: {
          ...params,
          sort: 'id,desc',
        },
        data: {
          ...data,
          level,
          builtIn: data.builtIn === 'true'
            ? true
            : data.builtIn === 'false'
              ? false
              : undefined,
          enabled: data.enabled === 'true'
            ? true
            : data.enabled === 'false'
              ? false
              : undefined,
        },
      }),
    },
    fields: [
      { name: 'name', type: 'string', label: '名称', required: true, validator: nameValidator },
      { name: 'code', type: 'string', label: '编码', required: true, validator: codeValidator },
      { name: 'level', type: 'string', label: '层级' },
      { name: 'builtIn', type: 'boolean', label: '来源' },
      { name: 'enabled', type: 'boolean', label: '状态' },
      { name: 'labels', type: 'auto', textField: 'name', valueField: 'id' },
      { name: 'gitlabLabelId', type: 'number', textField: 'name', valueField: 'id', required: level === 'project', options: gitlabLabelDs, label: 'Gitlab角色标签' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: '名称' },
      { name: 'code', type: 'string', label: '编码' },
      { name: 'builtIn', type: 'auto', label: '来源', textField: 'value', valueField: 'key', options: buildInDs },
      { name: 'enabled', type: 'auto', label: '状态', textField: 'value', valueField: 'key', options: enabledDs },
    ],
  };
};
