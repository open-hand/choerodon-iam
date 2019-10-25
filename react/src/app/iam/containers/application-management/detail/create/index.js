import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './stores';
import CreateView from './CreateView';

export default (props) => (
  <StoreProvider {...props}>
    <CreateView />
  </StoreProvider>
);
