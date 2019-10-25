export default (code, organizationId) => {
    return {
        autoQuery: true,
        selection: false,
        paging: false,
        transport: {
            read: {
                url: `/base/v1/paas_app_market/applications/${code}?organization_id=${organizationId}`,
                method: 'get',
            }
        },
        fields: [
            { name: "id", type: "number"},
            { name: 'marketAppCode', type: 'string' },
            { name: 'version', type: 'string' },
            { name: 'document', type: 'string' },
            { name: 'changelog', type: 'string' },
            { name: 'publishDate', type: 'string' },
            { name: 'versionCreationDate', type: 'string' },
            { name: 'downloadStatus', type: 'string' },
            { name: 'purchased', type: 'string' },
            { name: 'order', type: 'number' },
        ],
    };
};