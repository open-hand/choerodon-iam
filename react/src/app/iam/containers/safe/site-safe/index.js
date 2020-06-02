import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './store';
import PasswordPolicy from './password';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <PageWrap cache noHeader={[]}>
        <PageTab alwaysShow title="密码策略" tabKey="choerodon.code.site.security-password" component={PasswordPolicy} />
      </PageWrap>
    </StoreProvider>
  );
}

export default Index;
