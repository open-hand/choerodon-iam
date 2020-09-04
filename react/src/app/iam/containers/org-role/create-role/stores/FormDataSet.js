import isEmpty from 'lodash/isEmpty';
import { axios } from '@choerodon/boot';

function handleLoad({ dataSet }) {
  const record = dataSet.current;
  const roleLevel = record.get('level');
  const roleLabels = record.get('roleLabels');
  record.init('roleLevel', roleLevel);
  if (!isEmpty(roleLabels)) {
    record.init('roleLabels', roleLabels[0]);
  }
}

export default ({
  level, roleId, roleLabelsDs, organizationId, menuDs, formatMessage,
}) => {
  const codeValidator = async (value, name, record) => {
    if (record.status !== 'add') {
      return true;
    }
    const pa = /^[a-z]([-a-z0-9]*[a-z0-9])?$/;
    if (!value) {
      return formatMessage({ id: 'organization.role.code.require.msg' });
    }
    if (!pa.test(value)) {
      return formatMessage({ id: 'organization.role.code.pattern.msg' });
    }
    try {
      const res = await axios.get(`/iam/choerodon/v1/organizations/${organizationId}/roles/check_code_exist?code=${value}`);
      if (res) {
        return formatMessage({ id: 'organization.role.code.exist.msg' });
      }
      return true;
    } catch (err) {
      return '角色编码校验失败，请稍后再试';
    }
  };

  const nameValidator = (value) => {
    if (!value) {
      return formatMessage({ id: 'organization.role.name.require.msg' });
    }
    return true;
  };

  return {
    autoQuery: false,
    autoCreate: false,
    selection: false,
    paging: false,
    autoQueryAfterSubmit: false,
    transport: {
      read: {
        url: `/iam/choerodon/v1/organizations/${organizationId}/roles/${roleId}`,
        method: 'get',
      },
    },
    fields: [
      {
        name: 'name', type: 'string', label: '角色名称', required: true, validator: nameValidator,
      },
      {
        name: 'code', type: 'string', label: '角色编码', required: true, validator: codeValidator,
      },
      {
        name: 'roleLevel', type: 'string', label: '层级', defaultValue: level,
      },
      {
        name: 'roleLabels', type: 'object', label: 'GitLab角色标签', textField: 'name', valueField: 'id', required: level === 'project', options: roleLabelsDs,
      },
    ],
    events: {
      load: handleLoad,
    },
  };
};
