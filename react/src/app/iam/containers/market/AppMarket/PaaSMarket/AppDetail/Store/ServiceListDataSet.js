export default () => {
    return {
        autoQuery: false,
        dataKey: null,
        selection: false,
        paging: false,
        filterBar: false,
        fields: [
            { name: 'name', type: 'string', label: "应用服务名称" },
            { name: 'serviceVersionVOS', type: 'object', label: "应用服务版本" },
        ],
    };
};
