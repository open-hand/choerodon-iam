export default function ({ lovCode }) {
  return {
    selection: 'single',
    autoCreate: true,
    fields: [
      { name: 'code', type: 'object', lovCode },
    ],
  };
}
