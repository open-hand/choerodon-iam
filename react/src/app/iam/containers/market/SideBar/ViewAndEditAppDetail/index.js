import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { StoreProvider } from './Store';
import EditAppDetail from './EditAppDetail';
import ViewAppDetail from './ViewAppDetail';

const Index = (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    {props.mode === 'edit' ? <EditAppDetail /> : <ViewAppDetail />}
  </StoreProvider>
);

export default Index;
