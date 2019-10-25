import React from 'react';
import { StoreProvider } from './stores';
import FormView from './FormView';

function Index(props) {
  return (
    <StoreProvider {...props}>
      <FormView />
    </StoreProvider>
  );
}

export default Index;
