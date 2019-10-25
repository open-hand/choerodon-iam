import React from 'react';
import { SiderStoreProvider } from './stores';
import Sider from './Sider';

export default (props) => (
  <SiderStoreProvider {...props}>
    <Sider />
  </SiderStoreProvider>
);
