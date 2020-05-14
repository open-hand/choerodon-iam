import { axios } from '@choerodon/boot';
import pick from 'lodash/pick';

export default ({ level, prefix, roleId }) => {
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
  const nameValidator = (value) => {
    if (!value) {
      return '编码必输。';
    }
    if (value.trim() === '') {
      return '编码不能全为空格。';
    }
    return true;
  };

  return {
    autoQuery: false,
    autoCreate: false,
    selection: false,
    transport: {
      read: {
        url: `iam/choerodon/v1/roles/${roleId}`,
        method: 'get',
      },
      create: ({ data: [data] }) => {
        const res = pick(data, ['name', 'level']);
        res.code = `${prefix}${data.code}`;

        return ({
          url: 'iam/choerodon/v1/roles',
          method: 'post',
          data: res,
        });
      },
      update: ({ data: [data] }) => {
        const res = pick(data, ['code', 'name', 'objectVersionNumber', 'level']);

        return ({
          url: `/iam/choerodon/v1/roles/${data.id}`,
          method: 'put',
          data: res,
        });
      },
    },
    fields: [
      { name: 'name', type: 'string', label: '角色名称', required: true, validator: nameValidator },
      { name: 'code', type: 'string', label: '角色编码', required: true, validator: codeValidator },
      { name: 'level', type: 'string', label: '层级', defaultValue: level },
      { name: 'gitlabLabel', type: 'string', label: 'GitLab角色标签', required: level === 'project' },
    ],
  };
};
