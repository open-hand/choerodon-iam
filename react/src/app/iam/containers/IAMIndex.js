import React from 'react';
import { Route, Switch } from 'react-router-dom';
import { inject } from 'mobx-react';
import { ModalContainer } from 'choerodon-ui/pro';
import { asyncLocaleProvider, asyncRouter, nomatch } from '@choerodon/boot';

// global 对应目录
const siteSetting = asyncRouter(() => import('./site-setting'));
const menuSetting = asyncRouter(() => import('./global/menu-setting'));
const role = asyncRouter(() => import('./role'));
const siteUser = asyncRouter(() => import('./site-user'));
const rootUser = asyncRouter(() => import('./root-user'));

// organization
const orgRole = asyncRouter(() => import('./org-role'));
const orgUser = asyncRouter(() => import('./org-user'));
const organizationSetting = asyncRouter(() => import('./organization/organization-setting'));
const orgSafe = asyncRouter(() => import('./safe/org-safe'));

const siteSafe = asyncRouter(() => import('./safe/site-safe'));

const orgAdmin = asyncRouter(() => import('./org-admin'));

const orgClient = asyncRouter(() => import('./client'));

// project
const generalSetting = asyncRouter(() => import('./general-setting'));
const projectUser = asyncRouter(() => import('./project-user'));
const applicationSetting = asyncRouter(() => import('./application-setting'));
const applicationManagement = asyncRouter(() => import('./application-management'));

// user
const tokenManager = asyncRouter(() => import('./user/token-manager'));
const userInfo = asyncRouter(() => import('./user/user-info'));
const permissionInfo = asyncRouter(() => import('./user/permission-info'));

// saga 事务管理
const saga = asyncRouter(() => import('./saga/saga'));
const sagaInstance = asyncRouter(() => import('./saga/saga-instance'));

// 应用市场
const AppRelease = asyncRouter(() => import('./market/MarketRelease'));
const AppMarket = asyncRouter(() => import('./market/AppMarket'));

// lookup配置
const lookupConfig = asyncRouter(() => import('./lookup-config'));

// lov配置
const lovConfig = asyncRouter(() => import('./lov-config'));

// 多语言配置
const langConfig = asyncRouter(() => import('./lang-config'));

const orgOverview = asyncRouter(() => import('./org-overview'));

const platformOverview = asyncRouter(() => import('./platform-overview'));

const heroPage = asyncRouter(() => import('./hzero-page'));

@inject('AppState')
class IAMIndex extends React.Component {
  render() {
    const { match, AppState } = this.props;
    const langauge = AppState.currentLanguage;
    const IntlProviderAsync = asyncLocaleProvider(langauge, () => import(`../locale/${langauge}`));
    return (
      <IntlProviderAsync>
        <React.Fragment>
          <Switch>
            <Route path={`${match.url}/menu-setting`} component={menuSetting} />
            <Route path={`${match.url}/system-setting`} component={siteSetting} />
            <Route path={`${match.url}/role`} component={role} />
            <Route path={`${match.url}/org-role`} component={orgRole} />
            <Route path={`${match.url}/root-user`} component={rootUser} />
            <Route path={`${match.url}/team-member`} component={projectUser} />
            <Route path={`${match.url}/org-user`} component={orgUser} />
            <Route path={`${match.url}/user`} component={siteUser} />
            {/* <Route path={`${match.url}/general-setting`} component={generalSetting} /> */}
            <Route path={`${match.url}/project-setting/info`} component={generalSetting} />
            <Route path={`${match.url}/application-setting`} component={applicationSetting} />
            <Route path={`${match.url}/token-manager`} component={tokenManager} />
            <Route path={`${match.url}/user-info`} component={userInfo} />
            <Route path={`${match.url}/permission-info`} component={permissionInfo} />
            <Route path={`${match.url}/organization-setting`} component={organizationSetting} />
            <Route path={`${match.url}/saga`} component={saga} />
            <Route path={`${match.url}/saga-instance`} component={sagaInstance} />
            <Route path={`${match.url}/org-safe`} component={orgSafe} />
            <Route path={`${match.url}/safe`} component={siteSafe} />
            <Route path={`${match.url}/client`} component={orgClient} />
            <Route path={`${match.url}/org-admin`} component={orgAdmin} />
            <Route path={`${match.url}/org-overview`} component={orgOverview} />
            <Route path={`${match.url}/platform-overview`} component={platformOverview} />
            <Route path={`${match.url}/market-publish`} component={AppRelease} />
            <Route path={`${match.url}/app-market`} component={AppMarket} />
            <Route path={`${match.url}/application-management`} component={applicationManagement} />
            <Route path={`${match.url}/lookup-config`} component={lookupConfig} />
            <Route path={`${match.url}/lang-config`} component={langConfig} />
            <Route path={`${match.url}/lov-config`} component={lovConfig} />
            <Route path={`${match.url}/hzero/user`} component={heroPage} />
            <Route path={`${match.url}/hzero/role`} component={heroPage} />
            <Route path={`${match.url}/hzero/menu`} component={heroPage} />
            <Route path="*" component={nomatch} />
          </Switch>
          <ModalContainer />
        </React.Fragment>
      </IntlProviderAsync>
    );
  }
}

export default IAMIndex;
