import React, { Component, useContext } from 'react';
import { DataSet, Lov, Table, TextField, Form, Select, NumberField, Modal, Button } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import '../style/index.less';
import Store from './stores';

const { Column } = Table;
export default (() => {
  const { previewDataSet } = useContext(Store);
  return (
    <React.Fragment>
      <Lov dataSet={previewDataSet} name="code" />
    </React.Fragment>
    
  );
});
