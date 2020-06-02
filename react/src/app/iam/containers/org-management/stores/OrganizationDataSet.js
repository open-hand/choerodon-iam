import { DataSet } from 'choerodon-ui/pro';

export default ({ id = 0, intl }) => {
  const statusDataSet = new DataSet({
    data: [{
      value: 'true',
      meaning: intl.formatMessage({ id: 'enable' }),
    }, {
      value: 'false',
      meaning: intl.formatMessage({ id: 'disable' }),
    }],
  });

  return {
    autoQuery: true,
    selection: false,
    transport: {
      read: {
        url: '/iam/choerodon/v1/organizations',
        method: 'get',
      },
    },
    fields: [
      { name: 'name', type: 'string', label: '名称' },
      { name: 'homePage', type: 'string', label: '官网地址' },
      { name: 'code', type: 'string', label: '编码' },
      { name: 'projectCount', type: 'string', label: '项目数量' },
      { name: 'enabled', type: 'boolean', label: '状态' },
      { name: 'creationDate', type: 'string', label: '创建时间' },
      { name: 'ownerRealName', type: 'string', label: '所有者' },
    ],
    queryFields: [
      { name: 'name', type: 'string', label: '名称' },
      { name: 'code', type: 'string', label: '编码' },
      { name: 'enabled', type: 'string', label: '状态', options: statusDataSet },
      { name: 'ownerRealName', type: 'string', label: '所有者' },
    ],
  };
};
