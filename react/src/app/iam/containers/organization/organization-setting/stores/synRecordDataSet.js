export default ({ orgId }) => ({
  // autoCreate:true,
  autoQuery: true,
  selection: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/organizations/${orgId}/ldaps/history?sort=id%2Cdesc`,
      method: 'get',
    },
  },
  queryFields: [
    { name: 'syncBeginTime', type: 'string', label: '同步时间' },
    { name: 'errorUserCount', type: 'number', label: '失败人数' },
    { name: 'syncEndTime', type: 'string', label: '耗时' },
    { name: 'updateUserCount', type: 'number', label: '成功人数' },
  ],
  fields: [
    { name: 'syncBeginTime', type: 'string', label: '同步时间' },
    { name: 'errorUserCount', type: 'number', label: '失败人数' },
    { name: 'syncEndTime', type: 'string', label: '耗时' },
    { name: 'updateUserCount', type: 'number', label: '成功人数' },
  ],
});
