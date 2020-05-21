import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({

    async createRole(organizationId, data) {
      try {
        const res = await axios.post(`iam/choerodon/v1/organizations/${organizationId}/roles`, JSON.stringify(data));
        if (res && res.failed) {
          return false;
        }
        return true;
      } catch (e) {
        return false;
      }
    },

    async editRole(organizationId, data, roleId) {
      try {
        const res = await axios.put(`iam/choerodon/v1/organizations/${organizationId}/roles/${roleId}`, JSON.stringify(data));
        if (res && res.failed) {
          return false;
        }
        return true;
      } catch (e) {
        return false;
      }
    },
  }));
}
