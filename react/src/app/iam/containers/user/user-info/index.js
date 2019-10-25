import React from 'react';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';

const UserInfo = asyncRouter(() => import('./UserInfo'));

const Index = (props) => (
  <StoreProvider {...props}>
    <UserInfo />
  </StoreProvider>
);

export default Index;
