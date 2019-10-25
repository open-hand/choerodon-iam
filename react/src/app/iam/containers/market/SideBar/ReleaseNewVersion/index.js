import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './Store';
import ReleaseNewVersion from './ReleaseNewVersion';

const Index = (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    <ReleaseNewVersion />
  </StoreProvider>
);

export default Index;
