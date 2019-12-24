import React from 'react';
import Preview from './Preview';
import { StoreProvider } from './stores';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <Preview />
    </StoreProvider>
  );
}

export default Index;
