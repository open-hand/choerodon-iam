import React from 'react';
import { observer } from 'mobx-react-lite';
import DaysPicker from './DaysPicker';

const ContainerBlock = observer((props) => {
  const {
    children,
    width,
    height,
    title,
    hasDaysPicker,
    handleChangeDays,
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
        display: title ? 'flex' : 'none',
        justifyContent: 'space-between',
      }}
      >
        <span className="c7n-overview-containerBlock-title">{title}</span>
        { hasDaysPicker && <DaysPicker handleChangeDays={handleChangeDays} /> }
      </div>
      {children}
    </div>
  );
});

export default ContainerBlock;
