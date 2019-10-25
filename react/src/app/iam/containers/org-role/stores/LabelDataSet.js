export default ({ level }) => ({
  autoQuery: true,
  selection: 'single',
  transport: {
    read: {
      url: `/base/v1/labels?type=role&level=${level}`,
      method: 'get',
    },
  },
  fields: [
    { name: 'name', type: 'string' },
    { name: 'id', type: 'number', unique: true },
  ],
});
