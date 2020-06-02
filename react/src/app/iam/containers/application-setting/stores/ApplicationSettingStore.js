import { action, computed, observable } from 'mobx';
import { axios } from '@choerodon/boot';

class ApplicationSettingStore {
  @observable projectInfo = {};

  @observable imageUrl = null;

  @action setImageUrl(data) {
    this.imageUrl = data;
  }

  @computed get getImageUrl() {
    return this.imageUrl;
  }

  @action setApplicationInfo(data) {
    this.projectInfo = data;
  }

  @computed get getApplicationInfo() {
    return this.projectInfo;
  }

  axiosGetApplicationInfo(id) {
    return axios.get(`/iam/choerodon/v1/projects/${id}/applications/singleton`);
  }

  axiosSaveApplicationInfo(projectId, id, data) {
    return axios.put(`/iam/choerodon/v1/projects/${projectId}/applications/${id}`, data);
  }
}


export default ApplicationSettingStore;
