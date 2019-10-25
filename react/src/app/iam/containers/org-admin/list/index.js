import React from 'react';
import { PageWrap, PageTab } from '@choerodon/boot';
import { StoreProvider } from './stores';
import ListView from './ListView.js';

export default (props) => (
  <StoreProvider {...props}>
    <ListView />
  </StoreProvider>
);
