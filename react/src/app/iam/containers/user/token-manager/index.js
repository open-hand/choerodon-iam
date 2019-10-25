import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './stores';

// const index = asyncRouter(() => import('./TokenManager'), {
//   TokenManagerStore: () => import('../../../stores/user/token-manager'),
// });
const TokenManager = asyncRouter(() => import('./TokenManager'));
const Index = (props) => (
  <StoreProvider {...props}>
    <TokenManager />
  </StoreProvider>
);
export default Index;
