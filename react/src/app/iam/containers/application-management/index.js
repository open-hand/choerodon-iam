import React, { Component } from 'react';
import { Route, Switch } from 'react-router-dom';
import { nomatch } from '@choerodon/boot';
import List from './list';
import Detail from './detail';
// const List = asyncRouter(() => import('./list'));

const Index = ({ match }) => (
  <Switch>
    <Route path={`${match.url}/:applicationId`} component={Detail} />
    <Route path={match.url} component={List} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
