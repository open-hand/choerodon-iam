import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './Store';
import UpdateReleasedVersion from './UpdateReleasedVersion';

const Index = (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    <UpdateReleasedVersion />
  </StoreProvider>
);

export default Index;
