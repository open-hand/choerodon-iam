import React from 'react';
import { StoreProvider } from './stores';
import ProDeploy from './ProDeploy';

export default (props) => (
  <StoreProvider {...props}>
    <ProDeploy />
  </StoreProvider>
);
