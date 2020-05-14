import { axios, Choerodon } from '@choerodon/boot';

export default ({ id = 0, intl, orgRoleDataSet }) => {
  const username = intl.formatMessage({ id: 'username' });
  const loginName = intl.formatMessage({ id: 'loginname' });
  async function checkEmail(email) {
    try {
      const result = await axios.post(`/iam/choerodon/v1/projects/${id}/users/check`, JSON.stringify({ projectId: id, email }));
      if (result.failed) {
        return result.message;
      }
    } catch (e) {
      Choerodon.prompt(e);
    }
  }
  return {
    selection: false,
    transport: {
      create: {
        url: `/iam/choerodon/v1/projects/${id}/users`,
        method: 'post',
        transformRequest: (([data]) => {
          data.roles = data.roles.map((v) => ({ id: v }));
          return JSON.stringify(data);
        }),
      },
    },
    fields: [
      { name: 'realName', type: 'string', label: username, required: true },
      { name: 'roles', type: 'number', label: '角色', textField: 'name', valueField: 'id', required: true },
      { name: 'email', type: 'email', label: '邮箱', required: true, validator: checkEmail },
      { name: 'password', type: 'string', label: '密码', required: true },
    ],
  };
};
