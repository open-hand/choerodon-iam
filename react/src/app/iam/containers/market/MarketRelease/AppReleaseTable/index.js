import React from 'react';
import { ModalContainer } from 'choerodon-ui/pro';
import { PageTab, PageWrap } from '@choerodon/boot';
import { StoreProvider } from './Store';
import AppReleaseTable from './AppReleaseTable';

export default (props) => (
  // eslint-disable-next-line react/jsx-props-no-spreading

  <StoreProvider {...props}>
    <AppReleaseTable />
  </StoreProvider>
);
