import React from 'react';
import { SiderStoreProvider } from './stores';
import CreateSider from './CreateSider';
import RoleAssignSider from './RoleAssignSider';
import ModifySider from './ModifySider';
import ImportUserSider from './ImportUserSider';
import UserRoleSider from './UserRoleSider';
import ImportRoleSider from './ImportRoleSider';

export default (props) => (
  <SiderStoreProvider {...props}>
    {props.type === 'create' && <CreateSider />}
    {props.type === 'modify' && <ModifySider />}
    {props.type === 'roleAssignment' && <RoleAssignSider />}
    {props.type === 'importUser' && <ImportUserSider />}
    {props.type === 'addRole' && <UserRoleSider />}
    {props.type === 'importRole' && <ImportRoleSider />}
  </SiderStoreProvider>
);
