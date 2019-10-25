import { axios } from '@choerodon/boot';
import { DataSet } from 'choerodon-ui/pro';

export default {
  autoQuery: false,
  selection: false,
  paging: false,

  dataKey: null,
  // allAppServiceVersions: null
  // appServiceVersions: [{id: 489, version: "2018.9.25-172448-master", status: "unpublished"}]
  // code: "websocketapp"
  // id: 641
  // name: "websocketapp"
  // notPublishedOrFailerServiceVersion: null
  // type: "normal"
  fields: [
    { name: 'name', type: 'string', label: '应用服务名称' },
    { name: 'appServiceVersions' },
  ],
};
