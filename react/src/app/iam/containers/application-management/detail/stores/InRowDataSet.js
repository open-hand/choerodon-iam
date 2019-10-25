
export default (data) => {
  const name = 'name';
  
  return {
    autoQuery: false,
    selection: false,
    data,
    fields: [
      { name: 'name', type: 'string', label: '应用服务名称' },
      { name: 'code', type: 'string', label: '应用服务编码' },
      { name: 'type', type: 'string', label: '应用服务类型' },
      { name: 'appServiceVersions', type: 'string', label: '应用服务版本', textField: 'version', valueField: 'id' },
    ],
  };
};
