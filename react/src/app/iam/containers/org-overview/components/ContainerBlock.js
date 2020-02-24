import React from 'react';
import { observer } from 'mobx-react-lite';


const ContainerBlock = observer((props) => {
  const {
    children,
    width,
    height,
  } = props;
  return (
    <div
      className="c7n-overview-containerBlock"
      style={{
        width,
        height,
      }}
    >
      {children}
    </div>
  );
});

export default ContainerBlock;
