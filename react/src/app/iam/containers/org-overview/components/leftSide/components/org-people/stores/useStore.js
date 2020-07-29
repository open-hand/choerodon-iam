import { useLocalStore } from 'mobx-react-lite';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    orgPeopleChartData: {
      dateList: [],
      totalUserNumberList: [],
      newUserNumberList: [],
    },

    get getOrgPeopleChartData() {
      return this.orgPeopleChartData;
    },

    loading: false,

    async initOrgPeopleDataByDate(orgId, startTime, endTime) {
      this.loading = true;
      try {
        const data = await axios({
          method: 'GET',
          url: `/iam/choerodon/v1/organizations/${orgId}/users/count_by_date`,
          params: {
            start_time: startTime,
            end_time: endTime,
          },
        });
        if (data.failed) {
          throw data.message;
        }
        this.orgPeopleChartData = data;
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
