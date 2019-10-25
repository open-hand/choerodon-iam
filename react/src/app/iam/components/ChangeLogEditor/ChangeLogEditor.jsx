import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { DataSet, Table, TextField, NumberField, Modal, Button, Tabs } from 'choerodon-ui/pro';
import { inject, observer } from 'mobx-react';
import { injectIntl, FormattedMessage, IntlProvider } from 'react-intl';
import ReactMarkdown from 'react-markdown';
import CodeMirror from 'react-codemirror';
import 'codemirror/lib/codemirror.css';
import './ChangelogEditor.scss';
// @import (inline) './node_modules/codemirror/lib/codemirror.css';

const { TabPane } = Tabs;
const intlPrefix = 'project.app-dataset';
const cssPrefix = 'c7n-market-changelog';

@injectIntl
@observer
class ChangeLogEditor extends Component {
  updateCode = (newCode) => {
    const { current } = this.props;
    current.set('changelog', newCode);
  };

  render() {
    const { current, intl } = this.props;
    // const data = current ? current.get('changelog') : '';
    return current ? (
      <Tabs className={`${cssPrefix}`}>
        <TabPane className={`${cssPrefix}-edit`} tab="编辑" key="1">
          <p>在下面编辑您的发布日志，支持MarkDown格式</p>
          <CodeMirror
            value={current.get('changelog')}
            onChange={this.updateCode}
          />
        </TabPane>
        <TabPane className={`${cssPrefix}-preview`} tab="预览" key="2">
          <p>在下面预览您的发布日志</p>
          <ReactMarkdown source={current.get('changelog')} />
        </TabPane>
      </Tabs>
    ) : null;
  }
}

export default ChangeLogEditor;
