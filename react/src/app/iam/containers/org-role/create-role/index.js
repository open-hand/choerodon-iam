import React from 'react';
import { StoreProvider } from './stores';
import Content from './FormView';

export default (props) => (
  <StoreProvider {...props}>
    <Content />
  </StoreProvider>
);
