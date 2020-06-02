import { DataSet } from 'choerodon-ui/pro';

export default ({ intl, intlPrefix }) => {
  const statusDataSet = new DataSet({
    data: [{
      value: 'rejected',
      meaning: intl.formatMessage({ id: 'register.approval.rejected' }),
    }, {
      value: 'approved',
      meaning: intl.formatMessage({ id: 'register.approval.approved' }),
    }, {
      value: 'no_approval',
      meaning: intl.formatMessage({ id: 'register.approval.no_approval' }),
    }],
  });
  return {
    autoQuery: true,
    selection: false,
    fields: [
      { name: 'approvalStatus', type: 'string', label: '状态' },
      { name: 'userName', type: 'string', label: '姓名' },
      { name: 'userEmail', type: 'string', label: '邮箱' },
      { name: 'userPhone', type: 'string', label: '手机号' },
      { name: 'orgEmailSuffix', type: 'string', label: '域名' },
      { name: 'registerDate', type: 'string', label: '申请时间' },
    ],
    queryFields: [
      { name: 'approvalStatus', type: 'string', label: '状态', options: statusDataSet },
      { name: 'userName', type: 'string', label: '姓名' },
      { name: 'userEmail', type: 'string', label: '邮箱' },
      { name: 'userPhone', type: 'string', label: '手机号' },
      { name: 'orgEmailSuffix', type: 'string', label: '域名' },
    ],
    transport: {
      read: {
        url: '/iam/choerodon/v1/registers',
        method: 'get',
      },

      /**
         * 注册审批
         * @param data
         * @returns {{method: string, data: *, transformRequest: (function(*): string), url: string}}
         */
      update: data => ({
        url: `/iam/choerodon/v1/registers/approval/${data.data[0].id}`,
        method: 'post',
        data,
        transformRequest: req => JSON.stringify(req.data[0]),
      }),
    },
  };
};
