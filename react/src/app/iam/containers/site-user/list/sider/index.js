import React from 'react';
import { SiderStoreProvider } from './stores';
import RoleAssignSider from './RoleAssignSider';
import ModifySider from './ModifySider';
import ImportRoleSider from './ImportRoleSider';

export default (props) => (
  <SiderStoreProvider {...props}>
    {props.type === 'modify' && <ModifySider />}
    {props.type === 'roleAssignment' && <RoleAssignSider />}
    {props.type === 'importRole' && <ImportRoleSider />}
  </SiderStoreProvider>
);
