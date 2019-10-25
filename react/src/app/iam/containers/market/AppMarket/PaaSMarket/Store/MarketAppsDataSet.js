export default (orgId) => ({
    autoQuery: false,
    selection: false,
    transport: {
        read: {
            url: `/base/v1/paas_app_market?organizationId=${orgId}`,
            method: 'get',
        },
    },
    queryFields: [
        { name: 'name', type: 'string', label: '应用名称' },
        { name: 'categoryId', type: 'number', label: '应用类型id' },
        { name: 'contributor', type: 'string', label: '贡献者' },
        { name: 'isMyDownload', type: 'boolean', label: '我的下载', defaultValue: false },
        { name: 'orderBy', type: 'string', label: '排序字段' },
        { name: 'order', type: 'string', label: '排序（正序/倒序）', defaultValue: 'DESC' },
        { name: 'param', type: 'string', label: '模糊搜索字段' },
    ],
    fields: [
        { name: 'id', type: 'number' },
        { name: 'code', type: 'string' },
        { name: 'name', type: 'string' },
        { name: 'imageUrl', type: 'string' },
        { name: 'category', type: 'string' },
        { name: 'contributor', type: 'string' },
        { name: 'description', type: 'string' },
        { name: 'free', type: 'boolean' },
        { name: 'hasNewVersion', type: 'boolean' },
    ],
})

