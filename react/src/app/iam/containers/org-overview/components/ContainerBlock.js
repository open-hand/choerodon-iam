import React from 'react';
import { observer } from 'mobx-react-lite';


const ContainerBlock = observer((props) => {
  const {
    children,
    width,
    height,
    title,
  } = props;
  return (
    <div
      className="c7n-overview-containerBlock"
      style={{
        width,
        height,
      }}
    >
      <div style={{
        marginBottom: 10,
        display: title ? 'block' : 'none',
      }}
      >
        <span className="c7n-overview-containerBlock-title">{title}</span>
      </div>
      {children}
    </div>
  );
});

export default ContainerBlock;
