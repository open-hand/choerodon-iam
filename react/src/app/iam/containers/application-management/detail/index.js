import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './stores';
import ListView from './ListView';
import VersionListView from './VersionListView';

export default (props) => (
  <StoreProvider {...props}>
    <PageWrap noHeader={[]} cache>
      <PageTab title="应用服务" tabKey="tab1" component={ListView} alwaysShow />
      <PageTab title="应用版本" tabKey="tab2" component={VersionListView} alwaysShow />
    </PageWrap>
  </StoreProvider>
);
