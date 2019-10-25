import React from 'react';
import ListView from './list/ListView';
import { StoreProvider } from './store';

const Index = props => (
  <StoreProvider {...props}>
    <ListView />
  </StoreProvider>
);

export default Index;
