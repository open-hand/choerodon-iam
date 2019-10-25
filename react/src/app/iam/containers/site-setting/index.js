import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';
import './index.less';

const BasicInfo = asyncRouter(() => import('./basic-info'));

const Index = (props) => (
  <StoreProvider {...props}>
    <BasicInfo />
  </StoreProvider>
);

export default Index;
