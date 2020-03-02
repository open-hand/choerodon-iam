import React from 'react';
import { StoreProvider } from './stores';
import PlatformPeople from './PlatformPeople';

export default (props) => (
  <StoreProvider {...props}>
    <PlatformPeople />
  </StoreProvider>
);
