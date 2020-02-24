import React from 'react';
import { StoreProvider } from './stores';
import LeftSide from './LeftSide';

export default (props) => (
  <StoreProvider {...props}>
    <LeftSide />
  </StoreProvider>
);
