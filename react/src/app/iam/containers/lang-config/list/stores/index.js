import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import LangListDataSet from './LangListDataSet';
import LangCreateDataSet from './LangCreateDataSet';
import ServiceOptionsDataSet from './ServiceOptionsDataSet';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId }, getUserId: userId }, intl, children } = props;
    const intlPrefix = 'global.lang';
    const langOptions = [
      { text: '简体中文', value: 'zh_CN' },
      { text: 'English', value: 'en_US' },
    ];
    const langOptionsDs = useMemo(() => new DataSet({
      data: langOptions,
      selection: 'single',
    }));
    const serviceOptionsDataSet = useMemo(() => new DataSet(ServiceOptionsDataSet()));
    const langListDataSet = useMemo(() => new DataSet(LangListDataSet(serviceOptionsDataSet, langOptionsDs)));
    const langCreateDataSet = useMemo(() => new DataSet(LangCreateDataSet(langOptionsDs, serviceOptionsDataSet)));
    const value = {
      ...props,
      intlPrefix,
      langListDataSet,
      langCreateDataSet,
    };
    
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
