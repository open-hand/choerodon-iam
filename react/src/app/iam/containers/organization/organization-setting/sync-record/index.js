import React from 'react';
import { StoreProvider } from './store';
import SyncRecordForm from './SyncRecordForm';


function Index(props) {
  return (
    <StoreProvider {...props}>
      <SyncRecordForm />
    </StoreProvider>
  );
}
export default Index;
