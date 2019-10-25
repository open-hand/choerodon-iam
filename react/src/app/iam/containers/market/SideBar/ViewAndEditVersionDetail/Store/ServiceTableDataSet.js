import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

export default {
  autoQuery: false,
  selection: false,
  paging: false,
  dataKey: null,
  fields: [
    { name: 'name', type: 'string', label: '应用服务名称' },
    { name: 'appServiceVersions', type: 'object' },
  ],
};
