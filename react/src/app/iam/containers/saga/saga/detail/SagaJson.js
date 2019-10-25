import React from 'react';
import { observer } from 'mobx-react-lite';
import jsonFormat from '../../../../common/json-format';
import sagaStore from '../store/SagaImgStore';
import './style/index.less';

const prefixCls = 'c7n-saga';

const SagaJson = observer(() => (
  <div className={`${prefixCls}-detail-json`}>
    <pre>
      <code id="json">
        {jsonFormat(sagaStore.data)}
      </code>
    </pre>
  </div>
));

export default SagaJson;
