import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './stores';

const PermissionInfo = asyncRouter(() => import('./PermissionInfo'));
const Index = (props) => (
  <StoreProvider {...props}>
    <PermissionInfo />
  </StoreProvider>
);
export default Index;
