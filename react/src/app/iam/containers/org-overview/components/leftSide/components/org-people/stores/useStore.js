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

    async initOrgPeopleDataByDate(orgId, startTime, endTime) {
      const data = await axios({
        method: 'GET',
        url: `/iam/choerodon/v1/organizations/${orgId}/users/count_by_date`,
        params: {
          start_time: startTime,
          end_time: endTime,
        },
      });
      this.orgPeopleChartData = data;
    },
  }));
}
