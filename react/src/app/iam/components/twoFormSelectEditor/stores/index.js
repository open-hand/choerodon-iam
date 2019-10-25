import React, { createContext } from 'react';
import { observable } from 'mobx';

const Store = createContext();

export default Store;

export const StoreProvider = (props) => {
  const dsStore = observable([props.dsStore[0], []]);

  const { children } = props;
  const value = {
    ...props,
    dsStore,
  };
  return (
    <Store.Provider value={value}>
      {children}
    </Store.Provider>
  );
};
