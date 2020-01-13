import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './store';
import Password from './password';
import './index.less';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <PageWrap noHeader={[]}>
        <PageTab title="密码策略" tabKey="choerodon.code.organization.security-password" component={Password} />
      </PageWrap>
    </StoreProvider>
  );
}

export default Index;
