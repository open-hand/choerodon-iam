import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import index from './AppReleaseTable';

// const index = asyncRouter(() => import('./AppReleaseTable'));

const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={index} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
