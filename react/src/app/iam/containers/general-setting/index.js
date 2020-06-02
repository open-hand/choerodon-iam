
import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { asyncRouter, nomatch } from '@choerodon/boot';
import { withRouter } from 'react-router-dom';
import { PageWrap, PageTab } from '@choerodon/boot';
import GeneralSetting from './GeneralSetting';
import ApplicationSetting from '../application-setting/ApplicationSetting';

const TabIndex = () => (
  <PageWrap noHeader={[]} cache>
    <PageTab title="项目信息" tabKey="choerodon.code.project.general-info" component={withRouter(GeneralSetting)} alwaysShow />
    {/* <PageTab title="应用配置" tabKey="choerodon.code.project.general-application" component={withRouter(ApplicationSetting)} /> */}
  </PageWrap>
);
const Index = ({ match }) => (
  <Switch>
    <Route exact path={match.url} component={TabIndex} />
    <Route path="*" component={nomatch} />
  </Switch>
);

export default Index;
