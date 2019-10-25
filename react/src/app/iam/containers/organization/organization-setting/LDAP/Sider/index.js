import React from 'react';
import { StoreProvider } from './stores';
import LdapLoadClient from './LdapLoadClient';

export default function (props) {
  return (
    <StoreProvider {...props}>
      <LdapLoadClient />
    </StoreProvider>
  );
}
