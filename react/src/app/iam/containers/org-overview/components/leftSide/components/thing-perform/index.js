import React from 'react';
import { StoreProvider } from './stores';
import ThingPerform from './ThingPerform';

export default (props) => (
  <StoreProvider {...props}>
    <ThingPerform />
  </StoreProvider>
);
