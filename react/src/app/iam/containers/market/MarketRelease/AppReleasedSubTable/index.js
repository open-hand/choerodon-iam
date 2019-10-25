import React from 'react';
import { ModalContainer } from 'choerodon-ui/pro';
import { StoreProvider } from './Store';
import AppReleaseSubTable from './AppReleaseSubTable';

export default (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading
  <StoreProvider {...props}>
    <AppReleaseSubTable />
  </StoreProvider>
);
