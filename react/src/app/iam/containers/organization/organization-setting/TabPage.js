import React from 'react';
import { PageWrap, PageTab, asyncRouter } from '@choerodon/boot';
import BasicInfo from './basic-info';
import Ldap from './LDAP';
import WorkCalendarHome from './WorkCalendar';

export default function (props) {
  return (
    <PageWrap cache noHeader={[]}>
      <PageTab alwaysShow component={BasicInfo} title="组织信息" tabKey="choerodon.code.organization.general-info" />
      <PageTab alwaysShow component={Ldap} title="LDAP设置" tabKey="choerodon.code.organization.general-ldap" />
      <PageTab alwaysShow component={WorkCalendarHome} title="工作日历" tabKey="choerodon.code.organization.general-calendar" />
      <PageTab title="仓库" tabKey="choerodon.code.organization.general-repository" />
    </PageWrap>
  );
}
