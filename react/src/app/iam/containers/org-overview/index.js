import React from 'react';
import { StoreProvider } from './stores';
import OrgOverview from './OrgOverview';

export default (props) => (
  <StoreProvider {...props}>
    <OrgOverview />
  </StoreProvider>
);
