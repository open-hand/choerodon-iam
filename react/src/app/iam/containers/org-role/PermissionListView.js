import React, { useEffect, useMemo } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import PermissionListDataSet from './stores/PermissionListDataSet';

const { Column } = Table;

const PermissionListView = ({ record, permissionsArr, modal, onOk, edit }) => {
  const ds = useMemo(() => new DataSet(PermissionListDataSet()), []);

  function handleOk() {
    const selectedArr = ds.selected.map((r) => r.get('code'));
    onOk(selectedArr);
  }

  useEffect(() => {
    modal.handleOk(handleOk);
    ds.loadData(record.toData().permissions.filter((p) => p.permissionType !== 'page'));
    ds.forEach((r) => {
      r.selectable = edit;
      if (permissionsArr.includes(r.get('code'))) {
        r.isSelected = true;
      }
    });
  }, []);

  return (
    <Table dataSet={ds}>
      <Column name="code" />
      <Column name="description" />
    </Table>
  );
};

export default PermissionListView;
