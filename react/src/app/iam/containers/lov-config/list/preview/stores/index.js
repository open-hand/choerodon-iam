import React, { createContext, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import PreviewDataSet from './PreviewDataSet';

const Store = createContext();
export default Store;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl, lovCode } = props;
    
    const previewDataSet = useMemo(() => new DataSet(PreviewDataSet({ lovCode })), [lovCode]);
    const value = {
      ...props,
      intl,
      previewDataSet,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
