import React from 'react';
import { StoreProvider } from './Store';
import AppDetail from './AppDetail';

export default (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    <AppDetail />
  </StoreProvider>
);
