import React from 'react';
import { StoreProvider } from './stores';
import PlatformOverview from './PlatformOverview';

export default (props) => (
  <StoreProvider {...props}>
    <PlatformOverview />
  </StoreProvider>
);
