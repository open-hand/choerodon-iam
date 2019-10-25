import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './Store';
import ViewVersionDetail from './ViewVersionDetail';
import EditVersionDetail from './EditVersionDetail';

const Index = (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    {props.mode === 'edit' ? <EditVersionDetail /> : <ViewVersionDetail />}
  </StoreProvider>
);

export default Index;
