import React from 'react';
import { observer } from 'mobx-react-lite';
import DaysPicker from './DaysPicker';

import './ContainerBlock.less';
import Loading from '../../../components/loading';

const ContainerBlock = observer((props) => {
  const {
    children,
    width,
    height,
    title,
    hasDaysPicker,
    handleChangeDays,
    titleExtra,
    style,
    titleMarginBottom,
    loading,
  } = props;
  return (
    <div
      className="c7n-overview-containerBlock"
      style={{
        width,
        height,
        ...style,
      }}
    >
      <div style={{
        marginBottom: titleMarginBottom || 10,
        display: title ? 'flex' : 'none',
        justifyContent: 'space-between',
      }}
      >
        <span className="c7n-overview-containerBlock-title">{title}</span>
        <div className="c7n-overview-containerBlock-titleRight">
          <div style={{ marginRight: 24 }}>
            {titleExtra}
          </div>
          {hasDaysPicker && <DaysPicker handleChangeDays={handleChangeDays} />}
        </div>
      </div>
      {!loading ? children : <Loading display />}
    </div>
  );
});

export default ContainerBlock;
