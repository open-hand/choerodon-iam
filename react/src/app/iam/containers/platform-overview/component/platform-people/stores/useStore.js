import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    platformPeopleChartData: {
      dateList: [],
      totalUserNumberList: [],
      newUserNumberList: [],
    },

    get getPlatformPeopleChartData() {
      return this.platformPeopleChartData;
    },

    async initPlatformPeopleChartData(startTime, endTime) {
      const data = await axios({
        method: 'GET',
        url: '/iam/choerodon/v1/users/count_by_date',
        params: {
          start_time: startTime,
          end_time: endTime,
        },
      });
      this.platformPeopleChartData = data;
    },
  }));
}
