import { DataSet } from 'choerodon-ui/pro';
import { axios } from '@choerodon/boot';

export default function ({ intl, intlPrefix, lookupValueDataSet, CODE_REGULAR_EXPRESSION }) {
  async function checkCode(value, name, record) {
    if (!CODE_REGULAR_EXPRESSION.test(value)) {
      return '编码只能由字母、数字、"-"、"_"、"."组成，且只能以字母开头';
    }
    if (value === record.getPristineValue(name)) {
      return true;
    }
    try {
      const res = await axios.get(`/iam/choerodon/v1/lookups/check?code=${value}`);
      if (res.failed) {
        throw res.message;
      }
    } catch (err) {
      return err;
    }
  }
  return {
    autoQuery: true,
    selection: false,
    fields: [
      { name: 'code', type: 'string', label: '编码', validator: checkCode, required: true },
      { name: 'description', type: 'string', label: '描述', required: true },
    ],
    queryFields: [
      { name: 'code', type: 'string', label: '编码' },
    ],
    transport: {
      read: {
        url: '/iam/choerodon/v1/lookups',
        method: 'get',
      },
      update: data => ({
        url: `/iam/choerodon/v1/lookups/${data.data[0].id}`,
        method: 'put',
        data,
        transformRequest: req => JSON.stringify(req.data[0]),
      }),
      create: data => ({
        url: '/iam/choerodon/v1/lookups',
        method: 'post',
        data,
        transformRequest: req => JSON.stringify(req.data[0]),
      }),
      submit: data => ({
        url: `/iam/choerodon/v1/lookups/${data.data[0].id}`,
        method: 'put',
        data,
        transformRequest: req => JSON.stringify(req.data[0]),
      }),
    },
    children: {
      lookupValues: lookupValueDataSet,
    },
  };
}
