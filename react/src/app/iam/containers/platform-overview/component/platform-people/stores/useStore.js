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

    loading: false,
    setLoading(value) {
      this.loading = value;
    },

    async initPlatformPeopleChartData(startTime, endTime) {
      this.setLoading(true);
      try {
        const data = await axios({
          method: 'GET',
          url: '/iam/choerodon/v1/users/count_by_date',
          params: {
            start_time: startTime,
            end_time: endTime,
          },
        });
        if (data.failed) {
          throw data.message;
        }
        this.platformPeopleChartData = data;
        this.setLoading(false);
      } catch (e) {
        if (e && e.message) {
          return e.message;
        }
        return false;
      }
    },
  }));
}
