import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { SiderStoreProvider } from './stores';
import Sider from './Sider';

export default (props) => (
  <SiderStoreProvider {...props}>
    <Sider />
  </SiderStoreProvider>
);
