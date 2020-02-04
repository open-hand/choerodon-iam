import React, { useEffect, useMemo, useState } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { observer } from 'mobx-react-lite';
import PermissionListDataSet from './stores/PermissionListDataSet';

const { Column } = Table;

const PermissionListView = ({ record, permissionsArr, modal, onOk, edit }) => {
  const ds = useMemo(() => new DataSet(PermissionListDataSet()), []);
  const [filterParams, setFilterParams] = useState(null);

  function handleOk() {
    const selectedArr = ds.selected.map((r) => r.get('code'));
    onOk(selectedArr);
  }

  function handleUpdate({ value }) {
    if (value.length) {
      setFilterParams(value[0]);
    } else {
      setFilterParams(null);
    }
  }

  useEffect(() => {
    if (ds.queryDataSet) {
      ds.queryDataSet.addEventListener('update', handleUpdate);
    }
    
    modal.handleOk(handleOk);
    ds.loadData(record.toData().permissions.filter((p) => p.permissionType !== 'page'));
    ds.forEach((r) => {
      r.selectable = edit || r.get('required');
      if (permissionsArr.includes(r.get('code')) || r.get('required')) {
        r.isSelected = true;
      }
    });
  }, []);

  function tableFilter(r) {
    if (!filterParams) {
      return true;
    }
    return r.get('code').indexOf(filterParams) >= 0
      || r.get('description').indexOf(filterParams) >= 0;
  }

  return (
    <Table
      dataSet={ds}
      className="role-permissions-table role-table-sign"
      filterBarFieldName="params"
      filter={tableFilter}
    >
      <Column name="code" />
      <Column name="description" />
    </Table>
  );
};

export default observer(PermissionListView);
