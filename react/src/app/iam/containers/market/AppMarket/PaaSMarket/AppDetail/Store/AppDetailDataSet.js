export default (id, organizationId) => {
    return {
        autoQuery: true,
        selection: false,
        dataKey: null,
        paging: false,
        transport: {
            read: {
                url: `/base/v1/paas_app_market/${id}?organization_id=${organizationId}`,
                method: 'get',
            }
        },
        queryFields: [
            { name: 'version_id', type: 'number', label: '应用版本id' },
        ],
        fields: [
            { name: 'id', type: 'number' },
            { name: 'code', type: 'string' },
            { name: 'name', type: 'string' },
            { name: 'imageUrl', type: 'string' },
            { name: 'description', type: 'string' },
            { name: 'contributor', type: 'string' },
            { name: 'contributorUrl', type: 'string' },
            { name: 'overview', type: 'string' },
            { name: 'publishDate', type: 'string' },
            { name: 'latestVersion', type: 'string' },
            { name: 'latestVersionDate', type: 'string' },
            { name: 'free', type: 'boolean' },
            { name: 'categoryId', type: 'number' },
            { name: 'categoryName', type: 'string' },
            { name: 'type', type: 'string' },
            { name: 'downCount', type: 'number' },
            { name: 'document', type: 'string' },
            { name: 'enableDownload', type: 'boolean', defaultValue: true },
        ],
    };
};