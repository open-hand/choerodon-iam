import React from 'react';
import { StoreProvider } from './stores';
import RightSide from './RightSide';

export default (props) => (
  <StoreProvider {...props}>
    <RightSide />
  </StoreProvider>
);
