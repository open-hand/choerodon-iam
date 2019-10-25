
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';

const SagaIndex = asyncRouter(() => import('./saga'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={SagaIndex} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
