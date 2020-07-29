import { useLocalStore } from 'mobx-react-lite';
import moment from 'moment';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    chosenDay: 7,
    projectsArray: [],
    chartData: {
      dateList: [],
      projectDataList: [],
    },

    get getChosenDay() {
      return this.chosenDay;
    },

    setChosenDay(data) {
      this.chosenDay = data;
    },

    get getChartData() {
      return JSON.parse(JSON.stringify(this.chartData));
    },

    get getProjectsArray() {
      return this.projectsArray;
    },

    setProjectArray(data) {
      this.projectsArray = data;
    },

    initData(orgId, projectIds) {
      const startTime = moment().subtract(this.chosenDay, 'days').format('YYYY-MM-DD HH:mm:ss');
      const endTime = moment().format('YYYY-MM-DD HH:mm:ss');
      this.initChartData(orgId, projectIds, startTime, endTime);
    },

    loading: false,

    async initChartData(orgId, projectIds, startTime, endTime) {
      this.loading = true;
      try {
        const data = await axios({
          method: 'POST',
          url: `/iam/choerodon/v1/organizations/${orgId}/projects/deploy_records`,
          data: projectIds,
          params: {
            start_time: startTime,
            end_time: endTime,
          },
        });
        if (data.failed) {
          throw data.message;
        }
        this.chartData = data;
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
