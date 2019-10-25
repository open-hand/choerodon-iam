import React, { useContext, Fragment, useState, useCallback } from 'react';
import { Table } from 'choerodon-ui/pro';
import './index.less';
import { observer } from 'mobx-react-lite';


const { Column } = Table;
const SyncErrorForm = observer(({ dataSet2 }) => (
  <Fragment>
    <Table className="sync-record" dataSet={dataSet2}>
      <Column className="text-gray" name="uuid" />
      <Column className="text-gray" name="loginName" />
      <Column className="text-gray" name="realName" />
      <Column className="text-gray" name="email" />
      <Column className="text-gray" name="cause" />
    </Table>
  </Fragment>
));

export default SyncErrorForm;
