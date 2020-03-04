import { useLocalStore } from 'mobx-react-lite';

export default function useStore() {
  return useLocalStore(() => ({
    oldOptsRecord: [],
    setOldOptsRecord(data) {
      this.oldOptsRecord = data || [];
    },
    get getOldOptsRecord() {
      return this.oldOptsRecord;
    },
    pieChartRecord: [],
    lengendArr: [],
    leftDataArray: [],
    get getLeftDataArr() {
      return this.leftDataArray.slice();
    },
    get getLegendArr() {
      return this.lengendArr.slice();
    },
    colorStyle: ['#FD818DFF', '#6887E8FF', '#514FA0FF', '#F48590FF', '#6887E8FF', '#514FA0FF', '#6480DEFF', '#F48590FF', '#6887E8FF', '#CACAE4FF'],
    setLeftNum(data) {
      let leftAppNumber = 0;
      for (let i = 9; i < data.length; i += 1) {
        const item = data[i];
        leftAppNumber += item.appServerSum;
        this.leftDataArray.push(item);
      }
      return leftAppNumber;
    },
    get getPieRecord() {
      return this.pieChartRecord.slice();
    },
    setPieRecord(data) {
      if (data.length > 9) {
        const leftNumber = this.setLeftNum(data);
        for (let i = 0; i <= 9; i += 1) {
          const item = data[i];
          const obj = {
            value: i !== 9 ? item.appServerSum : leftNumber,
            name: i !== 9 ? item.projectName : '其他项目',
            itemStyle: {
              normal: { color: this.colorStyle[i] },
              emphasis: { color: this.colorStyle[i] },
            },
          };
          this.lengendArr.push(i !== 9 ? item.projectName : '其他项目');
          this.pieChartRecord.push(obj);
        }
      } else {
        for (let i = 0; i < data.length; i += 1) {
          const item = data[i];
          const obj = {
            value: item.appServerSum,
            name: item.projectName,
            itemStyle: {
              normal: { color: this.colorStyle[i] },
              emphasis: { color: this.colorStyle[i] },
            },
          };
          this.lengendArr.push(item.projectName);
          this.pieChartRecord.push(obj);
        }
      }
    },
  }));
}
