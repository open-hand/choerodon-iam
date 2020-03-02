import React from 'react';
import { StoreProvider } from './stores';
import OrgPeople from './OrgPeople';

export default (props) => (
  <StoreProvider {...props}>
    <OrgPeople />
  </StoreProvider>
);
