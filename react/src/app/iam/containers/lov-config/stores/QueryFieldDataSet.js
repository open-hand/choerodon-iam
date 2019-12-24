import { DataSet } from 'choerodon-ui/pro';

export default function ({ queryFieldTypeDs, booleanDs, queryFieldParamType }) {
  return {
    selection: false,
    fields: [
      // { name: 'lovCode', type: 'string', label: '列编码', required: true },
      { name: 'queryFieldDisplayFlag', type: 'boolean', label: '是否显示', defaultValue: true, options: booleanDs },
      { name: 'queryFieldRequiredFlag', type: 'boolean', label: '是否必填', defaultValue: false, options: booleanDs },
      { name: 'queryFieldLabel', type: 'string', label: 'placeholder' },
      { name: 'queryFieldName', type: 'string', label: '' },
      { name: 'queryFieldWidth', type: 'number', label: '组件宽度', min: 1, defaultValue: 150 },
      { name: 'queryFieldType', type: 'string', label: '搜索组件类型', required: true, options: queryFieldTypeDs },
      { name: 'queryFieldParamType', type: 'string', label: '参数类型', required: true, defaultValue: 'query', options: queryFieldParamType },
      { name: 'queryFieldLookupCode', type: 'string', label: 'LookUp', lookupUrl: '/base/v1/lookups', textField: 'code', valueField: 'code' },
      { name: 'queryFieldLovCode', type: 'string', label: 'LOV', lookupUrl: '/base/v1/lov/list', textField: 'code', valueField: 'code' },
      { name: 'queryFieldOrder', type: 'number', label: '组件排序号', defaultValue: 1 },
    ],
    events: {
      update({ dataSet, record, name, value, oldValue }) {
        if (name === 'queryFieldParamType' && value === 'path') {
          record.set('queryFieldDisplayFlag', false);
          record.set('queryFieldRequiredFlag', true);
        }
      },
    },
  };
}
