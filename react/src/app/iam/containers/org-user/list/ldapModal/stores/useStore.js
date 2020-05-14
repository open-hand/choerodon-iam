import { useLocalStore } from 'mobx-react-lite';
import { axios, Choerodon } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    tabKey: 'manual',
    setTabKey(data) {
      this.tabKey = data;
    },
    get getTabKey() {
      return this.tabKey;
    },

    syncUsers(orgId) {
      return axios.post(`/iam/choerodon/v1/organizations/${orgId}/ldaps/sync_users`);
    },

    stopSyncUsers(orgId) {
      return axios.put(`/iam/choerodon/v1/organizations/${orgId}/ldaps/stop`);
    },
  }));
}
