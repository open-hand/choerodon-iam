export default ({ orgId }) => ({
  autoQuery: false,
  selection: false,
  transport: {
    read: {
      url: `/base/v1/organizations/${orgId}/ldaps/history?sort=id%2Cdesc`,
      method: 'get',
    },
  },
  queryFields: [
    { name: 'syncBeginTime', type: 'string', label: '同步时间' },
  ],
  fields: [
    { name: 'syncBeginTime', type: 'string', label: '同步时间' },
    { name: 'errorUserCount', type: 'Number', label: '失败人数' },
    { name: 'syncEndTime', type: 'string', label: '耗时' },
    { name: 'updateUserCount', type: 'Number', label: '成功人数' },
    { name: 'newUserCount', type: 'Number', label: '新增人数' },
    { name: 'type', type: 'string', label: '同步类型' },
  ],
});
