import { action, computed, observable } from 'mobx';
import { axios } from '@choerodon/boot';

class SagaStore {
  @observable data = [];
  @observable tasks = [];

  @action
  setData(data) {
    this.data = data;
  }

  @computed
  get getData() {
    return this.data.slice();
  }

  @action
  setTask(tasks) {
    this.tasks = tasks;
  }

  @computed
  get getTasks() {
    return this.tasks.slice();
  }

  loadDetailData = async (id) => {
    const res = await axios.get(`/asgard/v1/sagas/${id}`);
    action(() => {
      this.data = res;
      this.tasks = res.tasks;
    });
  }
}

const sagaStore = new SagaStore();

export default sagaStore;
