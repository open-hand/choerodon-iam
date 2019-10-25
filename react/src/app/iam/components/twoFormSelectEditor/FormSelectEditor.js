import React, { useRef, useContext, useState, useEffect, useCallback } from 'react';
import { runInAction } from 'mobx';
import { Form, DataSet, Button, Icon } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import Store from './stores';
import './index.less';

export default observer(({ 
  name, 
  optionDataSetConfig,
  optionDataSet, 
  record, 
  children, 
  addButton, 
}) => {
  const formElement = useRef(null);

  async function handleSubmit({ dataSet, data }) {
    const result = await formElement.current.checkValidity();
    return result;
  }
  useEffect(() => {
    if (record[0] && record[1]) {
      record[0].dataSet.addEventListener('submit', handleSubmit);
      record[1].dataSet.addEventListener('submit', handleSubmit);
    }
    return () => {
      if (record[0] && record[1]) {
        record[0].dataSet.removeEventListener('submit', handleSubmit);
        record[1].dataSet.removeEventListener('submit', handleSubmit);
      }
    };
  });

  if (!record[0] || !record[1]) { return null; }
  const { dsStore } = useContext(Store);
  const valueField = [
    record[0] && record[0].fields.get(name[0]).get('valueField'),
    record[1] && record[1].fields.get(name[1]).get('valueField'),
  ];
  const textField = [
    record[0] && record[0].fields.get(name[0]).get('textField'),
    record[1] && record[1].fields.get(name[1]).get('textField'),
  ];
  
  function handleCreatOther() {
    runInAction(() => {
      record[0].set(name[0], (record[0].get(name[0]) || []).concat(''));
      record[1].set(name[1], (record[1].get(name[1]) || []).concat(''));
    });
  }

  const handleChange = [(e, index) => {
    const changedValue = record[0].get(name[0]);
    changedValue[index] = e;
    record[0].set(name[0], changedValue);
  }, (e, index) => {
    const changedValue = record[1].get(name[1]);
    changedValue[index] = e;
    record[1].set(name[1], changedValue);
  }];
  
  function handleDeleteItem(index) {
    const arr = [record[0].get(name[0]) || [], record[1].get(name[1]) || []];
    arr[0].splice(index, 1);
    arr[1].splice(index, 1);
    runInAction(() => {
      dsStore[0].splice(index, 1);
      dsStore[1].splice(index, 1);
      record[0].set(name[0], arr[0].slice());
      record[1].set(name[1], arr[1].slice());
    });
  }

  function addDisabled() {
    const someEmpty = record[0].get(name[0]).some(value => !value) || record[1].get(name[1]).some(value => !value);
    return someEmpty;
    // record[0].get(name[0]).length
  }

  return (
    <React.Fragment>
      <Form ref={formElement} className="two-form-select-editor" columns={13}>
        {(record[0].get(name[0]) || []).map((v, index) => {
          const value = [v, record[1].get(name[1])[index]];
          if (!dsStore[0][index]) {
            dsStore[0][index] = optionDataSet[0] || new DataSet(optionDataSetConfig[0]);
          }
          if (!dsStore[1][index]) {
            dsStore[1][index] = optionDataSet[1] || new DataSet(optionDataSetConfig[1]);
          }
          return [
            React.createElement(children[0], { 
              onChange: (text) => handleChange[0](text, index),
              value: value[0],
              options: optionDataSet[0] || dsStore[0][index],
              textField: textField[0],
              valueField: valueField[0],
              allowClear: false,
              clearButton: false,
              colSpan: 6,
              label: record[0].fields.get(name[0]).get('label'),
              required: record[0].fields.get(name[0]).get('required'),
            }),
            React.createElement(children[1], { 
              onChange: (text) => handleChange[1](text, index),
              value: value[1],
              options: optionDataSet[1] || dsStore[1][index],
              textField: textField[1],
              valueField: valueField[1],
              allowClear: false,
              clearButton: false,
              colSpan: 6,
              label: record[1].fields.get(name[1]).get('label'),
              required: record[1].fields.get(name[1]).get('required'),
            }),
            <Button 
              colSpan={1}
              className="two-form-select-editor-button"
              disabled={(record[0].get(name[0]) || []).length <= 1}
              onClick={() => handleDeleteItem(index)}
              icon="delete"
            />,    
          ];
        })}
      
      </Form>
      <Button
        colSpan={12}
        color={addDisabled() ? 'gray' : 'blue'}
        onClick={handleCreatOther}
        style={{ textAlign: 'left', marginTop: '-0.04rem' }}
        icon="add"
        disabled={addDisabled()}
      >
        {addButton || '添加'}
      </Button>
    </React.Fragment>
  );
});
