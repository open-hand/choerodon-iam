import { DataSet } from 'choerodon-ui/pro';
import { runInAction } from 'mobx';

export default function (type, projectId, versionOptionDataSet) {
  const setSelectedVersion = (JSONData) => {
    if (type === 'exist') {
      return JSON.parse(JSONData).map((item) => ({
        ...item,
        allAppServiceVersions: item.appServiceVersions instanceof Array ? item.appServiceVersions : [],
      }));
    }
    return JSON.parse(JSONData).list.map((item) => ({
      ...item,
      allAppServiceVersions: item.allAppServiceVersions instanceof Array ? item.allAppServiceVersions.map(v => ({ ...v, id: v.id.toString() })) : [],
    }));
  };

  const toggleVersionObjRequired = ({ record }) => ({
    required: type === 'exist',
  });

  return {
    dataKey: null,
    autoQuery: false,
    // autoCreate: true,
    paging: false,
    selection: type === 'new' ? 'multiple' : false,
    transport: {
      read: ({ data: { applicationId, versionId } }) => ({
        url: type === 'exist' ? `iam/choerodon/v1/projects/${projectId}/applications/${applicationId}/versions/${versionId}/svc_versions` : `iam/choerodon/v1/projects/${projectId}/applications/${applicationId}/services`,
        method: 'get',
        data: {},
        transformResponse: (data) => setSelectedVersion(data),
      }),
    },
    queryFields: [
      { name: 'versionObj', type: 'object', label: '应用版本', textField: 'version', valueField: 'id', options: versionOptionDataSet, dynamicProps: toggleVersionObjRequired },
      { name: 'applicationId', type: 'string', bind: 'versionObj.applicationId' },
      { name: 'versionId', type: 'string', bind: 'versionObj.id' },
      { name: 'versionName', type: 'string', bind: 'versionObj.version' },
    ],
    fields: [
      { name: 'name', type: 'string', label: '应用服务' },
      {
        name: 'appServiceVersions',
        type: 'object',
        label: '应用服务版本',
        dynamicProps: ({ record }) => ({
          options: new DataSet({
            paging: false,
            data: record.get('allAppServiceVersions'),
            selection: 'single',
          }),
        }),
        textField: 'version',
        valueField: 'id',
      },
    ],
    events: {
      load: ({ dataSet }) => {
        runInAction(() => {
          dataSet.forEach((item) => {
            item.set('appServiceVersions', item.get('allAppServiceVersions')[0]);
            if (type === 'new') {
              if (!item.get('allAppServiceVersions') || !item.get('allAppServiceVersions').length) {
                item.selectable = false;
              } else {
                dataSet.select(item);
              }
            }
          });
        });
      },
    },
  };
}
