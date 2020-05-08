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

    emailSuffix: null,
    get getEmailSuffix() {
      return this.emailSuffix;
    },
    setEmailSuffix(data) {
      this.emailSuffix = data;
    },

    async checkCreate(organizationId) {
      try {
        const res = await axios.get(`base/v1/organizations/${organizationId}/users/check_enable_create`);
        this.setCanCreate(res && !res.failed);
      } catch (e) {
        this.setCanCreate(false);
      }
    },

    async loadEmailSuffix(organizationId) {
      try {
        const res = await axios.get(`base/v1/organizations/${organizationId}/email_suffix`);
        if (res && !res.failed) {
          this.setEmailSuffix(res);
        } else {
          this.setEmailSuffix(null);
        }
      } catch (e) {
        this.setEmailSuffix(null);
      }
    },
  }));
}
