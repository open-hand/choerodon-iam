import React, { useEffect, useMemo } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import PermissionListDataSet from './stores/PermissionListDataSet';
import '../index.less';

const { Column } = Table;

const PermissionListView = ({ record }) => {
  const ds = useMemo(() => new DataSet(PermissionListDataSet()), []);

  useEffect(() => {
    ds.loadData(record.toData().permissions.filter((p) => p.permissionType !== 'page'));
  }, []);

  return (
    <Table dataSet={ds}>
      <Column className="text-gray" name="code" />
      <Column className="text-gray" name="description" />
    </Table>
  );
};

export default PermissionListView;
