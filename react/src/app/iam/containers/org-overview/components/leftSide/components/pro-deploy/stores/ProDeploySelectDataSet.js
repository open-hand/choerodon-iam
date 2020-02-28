export default ({ orgId, ProDeployStore }) => ({
  fields: [{
    name: 'proSelect',
    type: 'number',
    textField: 'name',
    valueField: 'id',
    multiple: true,
  }],
  events: {
    update: ({ record, value, oldValue }) => {
      if (value && value.length > 4) {
        record.set('proSelect', oldValue);
      } else {
        ProDeployStore.initData(orgId, value);
      }
    },
  },
});
