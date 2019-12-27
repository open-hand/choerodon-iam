import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';

export default function ({ CODE_REGULAR_EXPRESSION }) {
  async function checkCode(value, name, record) {
    if (!CODE_REGULAR_EXPRESSION.test(value)) {
      return '编码只能由字母、数字、"-"、"_"、"."组成，且只能以字母开头';
    }
    if (value === record.getPristineValue(name)) {
      return true;
    }
  }
  return {
    paging: false,
    selection: false,
    fields: [
      { name: 'code', type: 'string', label: '编码', required: true, validator: checkCode, unique: true },
      { name: 'description', type: 'string', label: '描述', required: true },
      { name: 'displayOrder', type: 'number', label: '顺序', required: true, defaultValue: 1 },
    ],
  };
}
