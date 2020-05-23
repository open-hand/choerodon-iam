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

    syncUsers(orgId, ldapId) {
      return axios.post(`/iam/v1/${orgId}/ldaps/${ldapId}/sync-users`);
    },

    stopSyncUsers(orgId, ldapId) {
      return axios.put(`/iam/v1/${orgId}/ldaps/${ldapId}/stop`);
    },
  }));
}
