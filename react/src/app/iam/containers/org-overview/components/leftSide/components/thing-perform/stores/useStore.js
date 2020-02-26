import { useLocalStore } from 'mobx-react-lite';
import moment from 'moment';
import { axios } from '@choerodon/boot';

export default function useStore() {
  return useLocalStore(() => ({
    chartData: {
      x: [],
      y: [],
    },

    get getChartData() {
      return JSON.parse(JSON.stringify(this.chartData));
    },

    async initThingPerformChartData(organizationId, date) {
      const data = await axios({
        method: 'GET',
        url: `asgard/v1/sagas/organizations/${organizationId}/instances/statistics/failure`,
        params: {
          date,
        },
      });
      const x = [];
      const y = [];
      data.forEach(d => {
        const { creationDate } = d;
        const newCreationDate = creationDate.split(' ')[0];

        function addData(xNum, yNum) {
          x.push(xNum);
          y.push(yNum);
        }

        if (x.length === 0) {
          addData(newCreationDate, d.count);
        } else {
          while (moment(x[x.length - 1]).add(1, 'days').format('YYYY-MM-DD') !== newCreationDate) {
            addData(moment(x[x.length - 1]).add(1, 'days').format('YYYY-MM-DD'), 0);
          }
          addData(newCreationDate, d.count);
        }
      });
      this.chartData = {
        x,
        y,
      };
    },
  }));
}
