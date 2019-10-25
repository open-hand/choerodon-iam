import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import moment from 'moment';
import { injectIntl } from 'react-intl';
import { DataSet } from 'choerodon-ui/pro';
import CalendarDataSet from './CalendarDataSet';
import HolidayDataSet from './HolidayDataSet';
import WorkDaySettingDataSet from './WorkDaySettingDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState')((props) => {
  const { children, AppState: { currentMenuType: { id: orgId, name } } } = props;
  const calendarDataSet = useMemo(() => new DataSet(CalendarDataSet({ orgId, year: moment().year() })), [orgId]);
  const workDaySettingDataSet = useMemo(() => new DataSet(WorkDaySettingDataSet({ orgId, year: moment().year(), calendarDataSet })), [orgId]);
  const holidayDataSet = useMemo(() => new DataSet(HolidayDataSet({ orgId, year: moment().year() })), [orgId]);

  const value = {
    ...props,
    orgId,
    calendarDataSet,
    holidayDataSet,
    workDaySettingDataSet,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
}));
