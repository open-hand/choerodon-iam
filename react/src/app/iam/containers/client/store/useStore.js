import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({

    loadClientDetail(organizationId, clientId) {
      return axios.get(`/iam/v1/${organizationId}/clients/${clientId}`);
    },
  }));
}
