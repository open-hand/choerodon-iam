import { DataSet } from 'choerodon-ui/pro/lib';

export default (currentToken, intl, intlPrefix) => {
  const redirectUri = intl.formatMessage({ id: `${intlPrefix}.redirect-uri` });
  const expire = intl.formatMessage({ id: 'status' });
  const createTime = intl.formatMessage({ id: `${intlPrefix}.create-time` });
  const expirationTime = intl.formatMessage({ id: `${intlPrefix}.expiration-time` });
  const clientId = intl.formatMessage({ id: `${intlPrefix}.client-id` });
  const handleForbidFist = ({ dataSet }) => {
    const record = dataSet.get(0);
    if (record.get('accesstoken') === currentToken) {
      record.selectable = false;
    }
  };
  const queryPredefined = new DataSet({
    autoQuery: true,
    paging: false,
    fields: [
      { name: 'key', type: 'string' },
      { name: 'value', type: 'string' },
    ],
    data: [
      { key: true, value: '正常' },
      { key: false, value: '已失效' },

    ],
  });


  return {
    autoQuery: true,
    paging: true,
    fields: [
      { name: 'accesstoken', type: 'string', label: 'token' },
      { name: 'redirectUri', type: 'string', label: redirectUri },
      { name: 'clientId', type: 'string', label: clientId },
      { name: 'createTime', type: 'string', label: createTime },
      { name: 'expirationTime', type: 'string', label: expirationTime },
      { name: 'expire', type: 'boolean', label: expire },


    ],
    queryFields: [
      { name: 'clientName', type: 'string', label: clientId },
    ],
    events: {
      load: handleForbidFist,
    },
    transport: {
      read: ({ data }) => ({
        url: '/iam/choerodon/v1/token',
        method: 'get',
        data: {
          ...data,
          currentToken,
        },
      }),
      destroy: ({ data, params }) => ({
        url: `/iam/choerodon/v1/token/batch?currentToken=${currentToken}`,
        method: 'delete',
      }),
    },
  };
};
