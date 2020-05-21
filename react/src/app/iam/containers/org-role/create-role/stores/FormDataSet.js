import isEmpty from 'lodash/isEmpty';

function handleLoad({ dataSet }) {
  const record = dataSet.current;
  const roleLevel = record.get('level');
  const roleLabels = record.get('roleLabels');
  record.init('roleLevel', roleLevel);
  if (!isEmpty(roleLabels)) {
    record.init('roleLabels', roleLabels[0]);
  }
}

export default ({ level, roleId, roleLabelsDs, organizationId, menuDs }) => {
  const codeValidator = (value, name, record) => {
    if (record.status !== 'add') {
      return true;
    }
    if (!value) {
      return '编码必输。';
    }
    if (value.trim() === '') {
      return '编码不能全为空格。';
    }
    if (value.length > 64) {
      return '编码长度不能超过64！';
    } else if (value.trim() === '') {
      return '编码不能全为空！';
    }
    const reg = /^[a-z]([-a-z0-9]*[a-z0-9])?$/;
    if (!reg.test(value)) {
      return '编码只能由小写字母、数字、"-"组成，且以小写字母开头，不能以"-"结尾。';
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
    paging: false,
    autoQueryAfterSubmit: false,
    transport: {
      read: {
        url: `/iam/choerodon/v1/organizations/${organizationId}/roles/${roleId}`,
        method: 'get',
      },
    },
    fields: [
      { name: 'name', type: 'string', label: '角色名称', required: true, validator: nameValidator },
      { name: 'code', type: 'string', label: '角色编码', required: true, validator: codeValidator },
      { name: 'roleLevel', type: 'string', label: '层级', defaultValue: level },
      { name: 'roleLabels', type: 'object', label: 'GitLab角色标签', textField: 'name', valueField: 'id', required: level === 'project', options: roleLabelsDs },
    ],
    events: {
      load: handleLoad,
    },
  };
};
