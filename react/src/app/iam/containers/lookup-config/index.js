import React from 'react';
import List from './list';
import { StoreProvider } from './stores';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <List />
    </StoreProvider>
  );
}

export default Index;
