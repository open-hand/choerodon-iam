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

    async initEmailSendByDate(startTime, endTime) {
      const data = await axios({
        method: 'GET',
        url: '/hmsg/choerodon/v1/mails/records/count_by_date',
        params: {
          start_time: startTime,
          end_time: endTime,
        },
      });
      this.emailSendData = data;
    },
  }));
}
