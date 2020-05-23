
export default ({ orgId, ldapId }) => ({
  // autoCreate:true,
  autoQuery: true,
  selection: false,
  transport: {
    read: {
      url: `/iam/v1/${orgId}/ldaps/${ldapId}/history?sort=id%2Cdesc`,
      method: 'get',
    },
  },
  queryFields: [],
  fields: [
    { name: 'syncBeginTime', type: 'string', label: '同步时间' },
    { name: 'errorUserCount', type: 'number', label: '失败人数' },
    { name: 'syncEndTime', type: 'string', label: '耗时' },
    { name: 'updateUserCount', type: 'number', label: '成功人数' },
    { name: 'newUserCount', type: 'number', label: '新增人数' },
  ],
});
