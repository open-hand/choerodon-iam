import React, { createContext, Children, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import LOVDataSet from './LOVDataSet';
import GridFieldDataSet from './GridFieldDataSet';
import QueryFieldDataSet from './QueryFieldDataSet';

const Store = createContext();
export default Store;
const CODE_REGULAR_EXPRESSION = /^[a-zA-Z][a-zA-Z0-9-_.]*$/;

export const StoreProvider = injectIntl(inject('AppState', 'HeaderStore')(
  (props) => {
    const { children, intl } = props;
    const booleanDs = new DataSet({
      data: [{
        meaning: '是',
        value: true,
      }, {
        meaning: '否',
        value: false,
      }],
    });
    const queryFieldParamType = new DataSet({
      data: [{
        meaning: 'query',
        value: 'query',
      }, {
        meaning: 'path',
        value: 'path',
      }],
    });
    const alignDs = new DataSet({
      data: [{
        meaning: '左对齐',
        value: 'left',
      }, {
        meaning: '右对齐',
        value: 'right',
      }, {
        meaning: '居中',
        value: 'center',
      }],
    });
    const resourceLevelDs = new DataSet({
      data: [{
        meaning: '平台',
        value: 'SITE',
      }, {
        meaning: '组织',
        value: 'ORGANIZATION',
      }, {
        meaning: '项目',
        value: 'PROJECT',
      }],
    });
    const queryFieldTypeDs = new DataSet({
      data: [{
        meaning: '文本输入框',
        value: 'TextFiled',
      }, {
        meaning: '数字输入框',
        value: 'NumberField',
      }, {
        meaning: '下拉框',
        value: 'Select',
      }, {
        meaning: '时间选择器',
        value: 'DateTimePicker',
      }, {
        meaning: '日期选择器',
        value: 'DatePicker',
      }, {
        meaning: 'lov',
        value: 'Lov',
      }],
    });
    async function checkCode(value, name, record) {
      if (!CODE_REGULAR_EXPRESSION.test(value)) {
        return '编码只能由字母、数字、"-"、"_"、"."组成，且只能以字母开头';
      }
      if (value === record.getPristineValue(name)) {
        return true;
      }
    }
    const intlPrefix = 'organization.pwdpolicy';
    const queryFieldDataSet = useMemo(() => new DataSet(QueryFieldDataSet({ queryFieldTypeDs, booleanDs, queryFieldParamType })), []);
    const gridFieldsDataSet = useMemo(() => new DataSet(GridFieldDataSet({ alignDs, queryFieldDataSet, checkCode })), []);
    const lovDataSet = useMemo(() => new DataSet(LOVDataSet({ intl, gridFieldsDataSet, queryFieldDataSet, booleanDs, resourceLevelDs, checkCode })), []);
    
    const modalStyle = {
      width: 'calc(100% - 3.5rem)',
    };
    const permissions = [
     
    ];
    const value = {
      ...props,
      intl,
      intlPrefix,
      modalStyle,
      lovDataSet,
      permissions,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
