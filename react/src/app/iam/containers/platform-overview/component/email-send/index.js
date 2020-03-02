import React from 'react';
import { StoreProvider } from './stores';
import EmailSend from './EmailSend';

export default (props) => (
  <StoreProvider {...props}>
    <EmailSend />
  </StoreProvider>
);
