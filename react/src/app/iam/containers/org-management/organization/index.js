
import React, { useContext } from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
// import Organization from './Organization';
import Store from '../stores';

const Organization = asyncRouter(
  () => import('./Organization'),
  () => import('../../../stores/global/organization'),
);

const Index = (props) => (
  <Store.Consumer>
    {({ AppState, HeaderStore, intl, organizationDataSet }) => <Organization intl={intl} AppState={AppState} HeaderStore={HeaderStore} organizationDataSet={organizationDataSet} />}
  </Store.Consumer>
);

export default Index;
