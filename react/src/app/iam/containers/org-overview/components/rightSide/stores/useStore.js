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
  }));
}
