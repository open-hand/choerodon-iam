import React, { createContext, useMemo } from 'react';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import Store from './ApplicationSettingStore';

const Context = createContext();
export default Context;
export const ContextProvider = injectIntl(inject('AppState')((props) => {
  const FieldVersionRef = {
    current: null,
  };
  const FieldFixVersionRef = {
    current: null,
  };
  const value = {
    ...props,
    prefixCls: 'c7n-iam-ApplicationSetting',
    intlPrefix: 'application.setting',
    store: useMemo(() => new Store(), []), // 防止update时创建多次store
    FieldVersionRef,
    FieldFixVersionRef,
    saveFieldVersionRef: (ref) => {
      FieldVersionRef.current = ref;
    },
    saveFieldFixVersionRef: (ref) => {
      FieldFixVersionRef.current = ref;
    },
  };

  return (
    <Context.Provider value={value}>
      {props.children}
    </Context.Provider>
  );
}));
