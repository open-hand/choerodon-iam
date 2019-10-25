import React from 'react';
import { PageWrap, PageTab, asyncRouter } from '@choerodon/boot';
import { StoreProvider } from './stores';
import Organization from './organization';
import OrganizationCategory from './organization-category';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <PageWrap noHeader={['choerodon.code.site.organization-approve', 'choerodon.code.site.organization-category']}>
        <PageTab title="组织清单" tabKey="choerodon.code.site.organization-list" component={Organization} />
        <PageTab title="组织类型" tabKey="choerodon.code.site.organization-category" component={OrganizationCategory} />
      </PageWrap>
    </StoreProvider>
  );
}

export default Index;
