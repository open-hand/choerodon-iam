import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const List = asyncRouter(() => import('./list'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={List} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
