import React from 'react';

import ListView from './list/ListView';
import { StoreProvider } from './store';


function index(props) {
  return (
    <StoreProvider {...props}>
      <ListView />
    </StoreProvider>
  );
}
export default index;
