import React from 'react';
import { PageWrap, PageTab, asyncRouter } from '@choerodon/boot';
import BasicInfo from './basic-info';
import Ldap from './LDAP';
import WorkCalendarHome from './WorkCalendar';

export default function (props) {
  return (
    <PageWrap noHeader={[]}>
      <PageTab route="/iam/organization-setting/info" component={BasicInfo} title="组织信息" tabKey="choerodon.code.organization.general-info" />
      <PageTab route="/iam/organization-setting/ldap" component={Ldap} title="LDAP设置" tabKey="choerodon.code.organization.general-ldap" />
      <PageTab route="/iam/organization-setting/working-calendar" component={WorkCalendarHome} title="工作日历" tabKey="choerodon.code.organization.general-calendar" />
    </PageWrap>
  );
}
