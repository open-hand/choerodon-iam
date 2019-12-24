import { DataSet } from 'choerodon-ui/pro';

export default function ({ alignDs, checkCode }) {
  return {
    selection: false,
    fields: [
      // { name: 'lovCode', type: 'string', label: '列编码', required: true },
      { name: 'gridFieldName', type: 'string', label: '列编码', required: true, unique: true, validator: checkCode },
      { name: 'gridFieldLabel', type: 'string', label: '列名称', required: true },
      { name: 'gridFieldOrder', type: 'number', label: '顺序', required: true, defaultValue: 1 },
      { name: 'gridFieldAlign', type: 'string', label: '对齐方式', required: true, defaultValue: 'left', options: alignDs },
      { name: 'gridFieldWidth', type: 'number', label: '列宽', min: 1, defaultValue: 150 },
      { name: 'gridFieldDisplayFlag', type: 'boolean', label: '是否显示', defaultValue: true },
      { name: 'gridFieldQueryFlag', type: 'boolean', label: '是否为查询字段', defaultValue: false },
    ],
  };
}
