export default function SagaDetailDataSet(id) {
  return {
    transport: {
      read: {
        url: `asgard/v1/sagas/instances/${id}/details`,
        method: 'get',
      },
    },
    fields: [
      { name: 'id', type: 'string' },
      { name: 'sagaCode', type: 'string' },
      { name: 'description', type: 'string' },
      { name: 'service', type: 'string' },
      { name: 'level', type: 'string' },
    ],
  };
}
