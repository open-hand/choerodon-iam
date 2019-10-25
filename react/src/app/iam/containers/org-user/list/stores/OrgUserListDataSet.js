import { axios, Choerodon } from '@choerodon/boot';

const regPhone = new RegExp(/^1[3-9]\d{9}$/);
const emptyReg = new RegExp(/^\s*$/);

export default ({ id = 0, intl, intlPrefix, safeOptionDs, statusOptionDs, orgRoleDataSet }) => {
  const username = intl.formatMessage({ id: 'username' });
  const loginName = intl.formatMessage({ id: 'loginname' });
  const status = intl.formatMessage({ id: `${intlPrefix}.status` });
  const safeStatus = intl.formatMessage({ id: `${intlPrefix}.safe-status` });
  async function check(value, name, record) {
    const organizationId = record.get('organizationId');
    if (value === record.getPristineValue(name) || !value) return;
    try {
      const result = await axios.post(`/base/v1/organizations/${organizationId}/users/check`, JSON.stringify({ organizationId, [name]: value }));
      if (result.failed) {
        return intl.formatMessage({ id: result.message });
      }
    } catch (e) {
      Choerodon.prompt(e);
    }
  }
  function checkPhone(value, name, record) {
    if (value === record.getPristineValue(name) || !value) return;
    if (!regPhone.test(value)) {
      return '手机号格式错误';
    }
    return check(value, name, record);
  }
  function checkRealname(value, name, record) {
    if (emptyReg.test(value)) {
      return '用户名不能全为空格';
    }
  }
  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: `/base/v1/organizations/${id}/users/search`,
        method: 'get',
      },
      create: {
        url: `/base/v1/organizations/${id}/users`,
        method: 'post',
        transformRequest: (([data]) => JSON.stringify(data)),
      },
      update: ({ data: editData }) => ({
        url: `/base/v1/organizations/${id}/users/${editData[0].id}`,
        method: 'put',
        transformRequest: (([data]) => JSON.stringify(data)),
      }),
    },
    fields: [
      { name: 'realName', type: 'string', label: username, required: true, validator: checkRealname },
      { name: 'loginName', type: 'string', label: loginName, unique: true },
      { name: 'enabled', type: 'boolean', label: status, textField: 'text', valueField: 'value', options: statusOptionDs },
      { name: 'roles', type: 'string', label: '角色', maxTagTextLength: 1, multiple: true, textField: 'name', valueField: 'id' },
      { name: 'locked', type: 'boolean', label: safeStatus, textField: 'text', valueField: 'value', options: safeOptionDs },
      { name: 'email', type: 'email', label: '邮箱', validator: check, required: true },
      { name: 'password', type: 'string', label: '密码' },
      { name: 'phone', type: 'string', label: '手机', validator: checkPhone, required: true },
      { name: 'language', type: 'string', label: '语言', defaultValue: 'zh_CN' },
      { name: 'timeZone', type: 'string', label: '时区', defaultValue: 'CTT' },
      { name: 'myRoles', type: 'string', label: '角色' },
    ],
    queryFields: [
      { name: 'realName', type: 'string', label: '用户名' },
      { name: 'loginName', type: 'string', label: '登录名' },
      { name: 'roleName', type: 'string', label: '角色', textField: 'name', valueField: 'name', options: orgRoleDataSet },
      { name: 'enabled', type: 'string', label: '启用状态', textField: 'text', valueField: 'value', options: statusOptionDs },
      { name: 'locked', type: 'string', label: '安全状态', textField: 'text', valueField: 'value', options: safeOptionDs },
    ],
  };
};
