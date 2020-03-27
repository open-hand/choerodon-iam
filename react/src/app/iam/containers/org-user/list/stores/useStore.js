import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    canCreate: false,
    get getCanCreate() {
      return this.canCreate;
    },
    setCanCreate(flag) {
      this.canCreate = flag;
    },

    async checkCreate(organizationId) {
      try {
        const res = await axios.get(`base/v1/organizations/${organizationId}/users/check_enable_create`);
        this.setCanCreate(res && !res.failed);
      } catch (e) {
        this.setCanCreate(false);
      }
    },
  }));
}
