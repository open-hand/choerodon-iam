import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    emailSendData: {
      dates: [],
      successNums: [],
      failedNums: [],
      totalNums: [],
    },

    get getEmailSendData() {
      return JSON.parse(JSON.stringify(this.emailSendData));
    },

    loading: false,

    async initEmailSendByDate(startTime, endTime) {
      this.loading = true;
      try {
        const data = await axios({
          method: 'GET',
          url: '/hmsg/choerodon/v1/mails/records/count_by_date',
          params: {
            start_time: startTime,
            end_time: endTime,
          },
        });
        if (data.failed) {
          throw data.message;
        }
        this.emailSendData = data;
        this.loading = false;
      } catch (e) {
        if (e && e.message) {
          return e.message;
        }
        return false;
      }
    },
  }));
}
