import { DataSet } from 'choerodon-ui/pro';

export default function ({ intl, queryFieldDataSet, gridFieldsDataSet, booleanDs, resourceLevelDs, checkCode }) {
  return {
    autoQuery: true,
    selection: false,
    fields: [
      { name: 'code', type: 'string', label: '编码', required: true, validator: checkCode },
      { name: 'description', type: 'string', required: true }, // 这个东西是placeholder，下面的placeholder是helper
      { name: 'placeholder', type: 'string', label: 'helper', help: 'helper是输入框的tip提示内容' }, // 这个东西是helper，上面的才是placeholder
      { name: 'resourceLevel', type: 'string', label: '层级', required: true, options: resourceLevelDs, defaultValue: 'SITE' },
      { name: 'permissionCode',
        type: 'string',
        label: 'API',
        required: true,
        dynamicProps: ({ record }) => ({ lookupUrl: `/iam/choerodon/v1/lov/api?level=${record.get('resourceLevel') || 'SITE'}` }),
        textField: 'code',
        valueField: 'code' },
      { name: 'valueField', type: 'string', label: 'Value Field', required: true },
      { name: 'textField', type: 'string', label: 'Text Field', required: true },
      { name: 'editFlag', type: 'boolean', label: '是否可编辑', default: false, options: booleanDs },
      { name: 'multipleFlag', type: 'boolean', label: '是否多选', default: false, options: booleanDs },
      { name: 'title', type: 'string', label: 'LOV模态框标题', required: true },
      { name: 'height', type: 'number', label: 'LOV模态框高度', required: true, min: 1, defaultValue: 500 },
      { name: 'width', type: 'number', label: 'LOV模态框宽度', required: true, min: 1, defaultValue: 500 },
      { name: 'treeFlag', type: 'boolean', label: '是否为树形结构', default: false, options: booleanDs },
      {
        name: 'idField',
        type: 'string',
        label: '子字段',
        dynamicProps: ({ record }) => ({
          required: record.get('treeFlag'),
        }),
      },
      {
        name: 'parentField',
        type: 'string',
        label: '父字段',
        dynamicProps: ({ record }) => ({
          required: record.get('treeFlag'),
        }),
      },
      { name: 'pageFlag', type: 'boolean', label: '是否分页', defaultValue: true, options: booleanDs },
      {
        name: 'pageSize',
        type: 'number',
        label: '每页记录数',
        defaultValue: 10,
        dynamicProps: ({ record }) => ({
          required: record.get('pageFlag'),
        }),
      },
    ],
    queryFields: [
      { name: 'code', type: 'string', label: 'lov编码' },
    ],
    transport: {
      read: {
        url: '/iam/choerodon/v1/lov/list',
        method: 'get',
      },
      update: data => ({
        url: `/iam/choerodon/v1/lov/${data.data[0].id}`,
        method: 'put',
        data,
        transformRequest: req => JSON.stringify(req.data[0]),
      }),
      create: data => ({
        url: '/iam/choerodon/v1/lov',
        method: 'post',
        data,
        transformRequest: req => {
          req.data[0].queryFields = req.data[0].queryFields.map(field => ({ ...field, lovCode: req.data[0].code }));
          req.data[0].gridFields = req.data[0].gridFields.map(field => ({ ...field, lovCode: req.data[0].code }));
          return JSON.stringify(req.data[0]);
        },
      }),
    },
    children: {
      gridFields: gridFieldsDataSet,
      queryFields: queryFieldDataSet,
    },
    events: {
      update: ({ name, record }) => {
        if (name === 'resourceLevel') {
          record.init('permissionCode');
        }
      },
    },
  };
}
