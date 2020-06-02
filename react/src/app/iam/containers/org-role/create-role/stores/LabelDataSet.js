export default () => ({
  autoQuery: false,
  selection: 'single',
  transport: {
    read: {
      url: '/iam/choerodon/v1/labels/project_gitlab_labels',
      method: 'get',
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
