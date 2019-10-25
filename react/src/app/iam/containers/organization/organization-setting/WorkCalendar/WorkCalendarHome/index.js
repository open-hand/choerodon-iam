import React from 'react';
import WorkCalendarHome from './WorkCalendarHome';
import { StoreProvider } from './stores';

const Index = (props) => (
  <StoreProvider {...props}>
    <WorkCalendarHome />
  </StoreProvider>
);

export default Index;
