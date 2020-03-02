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
    oldNoticeRecord: [],
    setOldNoticeRecord(data) {
      this.oldNoticeRecord = data || [];
    },
    get getOldNoticeRecord() {
      return this.oldNoticeRecord;
    },
  }));
}
