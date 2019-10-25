import React from 'react';
import { StoreProvider } from './Store';
import MainPage from './MainPage';

export default (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    <MainPage />
  </StoreProvider>
);
