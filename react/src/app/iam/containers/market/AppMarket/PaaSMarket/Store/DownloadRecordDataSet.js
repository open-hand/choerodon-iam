import { DataSet } from 'choerodon-ui/pro';

const statusOptionDataSet = new DataSet({
  autoQuery: false,
  selection: false,
  fields: [
    { name: 'key', type: 'string' },
    { name: 'value', type: 'string' },
  ],
  data: [
    { key: 'completed', value: '下载成功' },
    { key: 'downloading', value: '下载中' },
    { key: 'failed', value: '下载失败' },
  ],
});

export default (orgId) => ({
  autoQuery: false,
  selection: false,
  transport: {
    read: {
      url: `/iam/choerodon/v1/applications/download_records?organization_id=${orgId}`,
      method: 'get',
    },
  },
  queryFields: [
    { name: 'appName', type: 'string', label: '应用名称' },
    { name: 'categoryName', type: 'string', label: '应用类型' },
    { name: 'downloader', type: 'string', label: '下载人' },
    { name: 'versionName', type: 'string', label: '下载版本' },
    { name: 'status', type: 'string', label: '下载状态', textField: 'value', valueField: 'key', options: statusOptionDataSet },
    { name: 'params', type: 'string', label: '模糊查询参数' },
  ],
  fields: [
    { name: 'id', type: 'string', label: 'id' },
    { name: 'mktAppName', type: 'string', label: '应用名称' },
    { name: 'mktAppCode', type: 'string', label: '应用编码' },
    { name: 'mktAppId', type: 'number', label: 'appId' },
    { name: 'mktAppImageUrl', type: 'string', label: '应用图标' },
    { name: 'categoryName', type: 'string', label: '应用类别' },
    { name: 'mktVersionId', type: 'number', label: '应用版本id' },
    { name: 'mktVersionName', type: 'string', label: '下载版本' },
    { name: 'createdBy', type: 'number', label: '下载人id' },
    { name: 'downloaderImgUrl', type: 'string', label: '下载人头像' },
    { name: 'downloaderLoginName', type: 'string', label: '下载人登录名' },
    { name: 'downloaderRealName', type: 'string', label: '下载人' },
    { name: 'creationDate', type: 'string', label: '下载时间' },
    { name: 'status', type: 'string', label: '下载状态' },
  ],
});
