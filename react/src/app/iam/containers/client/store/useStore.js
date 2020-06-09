import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({

    loadClientDetail(organizationId, clientId) {
      return axios.get(`/iam/v1/${organizationId}/clients/${clientId}`);
    },

    async loadClientRoles(clientId) {
      try {
        const res = await axios.get(`/iam/hzero/v1/member-roles/client-roles/${clientId}?memberType=client&page=0&size=0`);
        if (res && res.content) {
          return res.content;
        } else {
          return false;
        }
      } catch (e) {
        return false;
      }
    },
  }));
}
