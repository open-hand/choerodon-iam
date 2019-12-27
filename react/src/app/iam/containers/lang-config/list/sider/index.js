import React from 'react';
import CreateSider from './CreateSider';
import ModifySider from './ModifySider';

export default (props) => {
  if (props.type === 'create') {
    return <CreateSider {...props} />;
  } else {
    return <ModifySider {...props} />;
  }
};
