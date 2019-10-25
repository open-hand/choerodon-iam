import React, { useState, useEffect, useMemo, useCallback, useRef } from 'react';
import remove from 'lodash/remove';
import { observer } from 'mobx-react-lite';
import { Action, axios } from '@choerodon/boot';
import { Tabs, Icon, message, Checkbox, Popover } from 'choerodon-ui';
import { Table, Form, TextField, DataSet, Modal, Button, Select } from 'choerodon-ui/pro';
import FormSelectEditor from '../../components/formSelectEditor';
import FormListDataSet from './stores/FormListDataSet';
import LabelDataSet from './stores/LabelDataSet';
import PermissionListView from './PermissionListView';

const { Column } = Table;
const { TabPane } = Tabs;

const LEVEL_OBJ = {
  site: ['site', 'user'],
  project: ['project'],
  organization: ['organization'],
};
const LEVEL_NAME = {
  site: '全局层',
  organization: '组织层',
  project: '项目层',
  user: '个人中心',
};
const modalStyle = {
  width: 'calc(100% - 3.5rem)',
};
const modalKey = Modal.key();

const ListView = ({ context, level, modal, base }) => {
  const { listDataSet: dataSet, labelTipDataSet } = context;
  const { status } = dataSet.current;
  const prefix = `role/${level}/default/`;

  const [tabLevel, setTabLevel] = useState(level);
  const [permissionsArr, setPermissionsArr] = useState([]);
  const edit = useRef(true);
  const modalPermission = useRef(null);
  
  const ds = useMemo(() => new DataSet(FormListDataSet({ level })), [level]);

  const userDs = useMemo(() => {
    if (level === 'site') {
      return new DataSet(FormListDataSet({ level: 'user' }));
    } else {
      return null;
    }
  }, [level]);

  const wrapSetPermissionArr = useCallback(({ dataSet: dataset }) => {
    if (status === 'add') {
      let arr = [];
      dataset.forEach((r) => {
        const data = r.toData();
        arr = arr.concat(data.permissions.map((p) => p.code));
      });
      const combineArr = permissionsArr.concat(arr);
      setPermissionsArr([...new Set(combineArr)]);
    }
  }, [permissionsArr, ds, userDs]);

  useEffect(() => {
    if (status === 'add' && (!base || !base.length)) {
      ds.addEventListener('load', wrapSetPermissionArr);
      if (level === 'site') {
        userDs.addEventListener('load', wrapSetPermissionArr);
      }
      return () => {
        ds.removeEventListener('load', wrapSetPermissionArr);
        if (level === 'site') {
          userDs.removeEventListener('load', wrapSetPermissionArr);
        }
      };
    }
  }, [wrapSetPermissionArr, level]);

  async function loadRole() {
    const record = dataSet.current;
    if (status !== 'add') {
      const res = await axios.get(`base/v1/roles/${record.get('id')}`);
      edit.current = !res.builtIn;
      setPermissionsArr(res.permissions.map((p) => p.code));
      record.set('labels', res.labels);
      if (res.builtIn) {
        ds.forEach((r) => {
          r.selectable = false;
        });
        if (userDs) {
          userDs.forEach((r) => {
            r.selectable = false;
          });
        }
      }
    }
    if (base && base.length) {
      const res = await axios.post('/base/v1/permissions', base);
      setPermissionsArr([...new Set(res.map((p) => p.code))]);
    }
  }

  const handleOkRole = useCallback(async () => {
    const record = dataSet.current;
    if (await record.validate() !== false) {
      if (!permissionsArr.length) {
        message.error('至少包含一个权限。');
        return false;
      }
      const roleObj = record.toData();
      const role = {
        name: roleObj.name,
        code: status === 'add' ? `${prefix}${roleObj.code}` : roleObj.code,
        level,
        permissions: permissionsArr.map((p) => ({ code: p })),
        labels: roleObj.labels ? roleObj.labels.map((id) => ({ id })) : undefined,
        objectVersionNumber: roleObj.objectVersionNumber,
      };
      let res;
      if (status === 'add') {
        res = await axios.post('base/v1/roles', role);
      } else {
        res = await axios.put(`/base/v1/roles/${roleObj.id}`, role);
      }
      if (res && !res.failed) {
        message.success(status === 'add' ? '创建成功' : '修改成功');
        dataSet.query();
        return true;
      } else {
        message.error(res.message);
        return false;
      }
    } else {
      return false;
    }
  }, [permissionsArr]);

  useEffect(() => {
    modal.handleOk(handleOkRole);
  }, [handleOkRole]);

  useEffect(() => {
    loadRole();
  }, []);

  function getTabCodes() {
    return LEVEL_OBJ[level];
  }

  function renderCode({ value }) {
    if (status !== 'add') {
      return value;
    }
    if (!value) return undefined;
    return `${prefix}${value}`;
  }

  function renderLabel() {
    const content = (
      <ul className="role-label-ul">
        {
          labelTipDataSet.map(r => (
            <li>
              <span>{r.get('name')}</span>
              <span>{r.get('description')}</span>
            </li>
          ))
        }
      </ul>
    );
    return (
      <div style={{ width: '5.12rem' }}>
        <FormSelectEditor
          record={dataSet.current}
          optionDataSetConfig={LabelDataSet({ level })}
          name="labels"
          addButton="添加其他角色标签"
        >
          {((itemProps) => (
            <div style={{ position: 'relative' }}>
              <Select 
                {...itemProps}
                label="角色标签"
                labelLayout="float"
                searchable
                style={{ width: '100%' }}
              />
              <Popover content={content} placement="bottom">
                <Icon type="help" style={{ position: 'absolute', right: '.26rem', top: '.08rem', zIndex: 1 }} />
              </Popover>
            </div>
          ))}
        </FormSelectEditor>
      </div>
    );
  }

  function renderForm() {
    return (
      <Form style={{ width: '5.12rem' }} record={dataSet.current} columns={2} className="c7n-role-msg-form">
        <TextField name="code" style={{ width: '2.48rem', marginRight: '.16rem' }} disabled={status !== 'add'} renderer={renderCode} />
        <TextField name="name" style={{ width: '2.48rem' }} disabled={!edit.current} />
      </Form>
    );
  }

  const handleOk = useCallback((selectedPermissionsArr) => {
    if (!edit.current) {
      return;
    }
    const { current } = ds;
    const recordPermissionsArr = current.toData().permissions.filter((p) => p.permissionType !== 'page').map((p) => p.code);
    let sp = permissionsArr.slice();
    sp = sp.concat(selectedPermissionsArr);
    remove(sp, (p) => recordPermissionsArr.includes(p) && !selectedPermissionsArr.includes(p));
    sp = [...new Set(sp)];
    setPermissionsArr(sp);
  }, [permissionsArr]);

  function handleClickRecord(record) {
    modalPermission.current = Modal.open({
      key: modalKey,
      drawer: true,
      title: (
        <div style={{ display: 'flex', alignItems: 'center' }} onClick={() => modalPermission.current.close()}>
          <Button shape="circle" funcType="flat" icon="arrow_back" style={{ marginRight: 10 }} />
          权限配置
        </div>
      ),
      children: (
        <PermissionListView record={record} permissionsArr={permissionsArr} onOk={handleOk} edit={edit.current} />
      ),
      style: modalStyle,
    });
  }

  function getChildPermissions(record, res) {
    if (record.children && record.children.length) {
      record.children.forEach((r) => getChildPermissions(r, res));
    } else {
      res.res = res.res.concat(record.toData().permissions);
    }
  }

  function getAllChildPermissions(record) {
    const res = { res: [] };
    record.children.forEach((r) => {
      getChildPermissions(r, res);
    });
    return [...new Set(res.res)];
  }

  function renderCount({ record }) {
    if (record.children && record.children.length) {
      return null;
    }
    const recordPermissions = record.data.permissions;
    const filteredRecordPermissions = recordPermissions.filter((p) => p.permissionType !== 'page');
    const filteredRecordSelectPermissions = [
      ...new Set(recordPermissions.filter((p) => p.permissionType !== 'page').map((p) => p.code)),
    ].filter((pcode) => permissionsArr.includes(pcode));
    return `(${filteredRecordSelectPermissions.length}/${filteredRecordPermissions.length})`;
  }

  const handleChangeCheck = useCallback((record, e) => {
    const { checked } = e.target;
    const recordPermissions = record.children && record.children.length ? getAllChildPermissions(record) : record.data.permissions;
    const recordPermissionsCode = recordPermissions.filter((p) => p.permissionType === 'page').map((p) => p.code);
    if (checked) {
      setPermissionsArr([...new Set(permissionsArr.concat(recordPermissionsCode))]);
    } else {
      let sp = permissionsArr.slice();
      remove(sp, (p) => recordPermissionsCode.includes(p) && permissionsArr.includes(p));
      sp = [...new Set(sp)];
      setPermissionsArr(sp);
    }
  }, [permissionsArr]);

  function renderCheck({ record }) {
    const recordPermissions = record.children && record.children.length ? getAllChildPermissions(record) : record.data.permissions;
    const recordRoutePermissions = recordPermissions.filter((p) => p.permissionType === 'page');
    const isChecked = recordRoutePermissions.map((p) => p.code).some((pCode) => permissionsArr.includes(pCode));
    return (
      <Checkbox
        checked={isChecked}
        onChange={(e) => handleChangeCheck(record, e)}
        disabled={dataSet.current.get('builtIn')}
      />
    );
  }

  function renderName({ record }) {
    const { icon, name } = record.toData();
    return (
      <React.Fragment>
        <Icon type={icon} style={{ marginRight: '.08rem', lineHeight: '.32rem', verticalAlign: 'top' }} />
        {name}
      </React.Fragment>
    );
  }

  function renderAction({ record }) {
    if (record.get('type') === 'menu_item') {
      const actionDatas = [
        { service: [], text: '权限配置', action: () => handleClickRecord(record) },
      ];
      return <Action data={actionDatas} />;
    }
    return null;
  }

  function renderTable() {
    return (
      <Table
        dataSet={tabLevel !== 'site' && level === 'site' ? userDs : ds}
        queryBar="none"
        mode="tree"
        buttons={['expandAll', 'collapseAll']}
        expandIconColumnIndex={1}
      >
        <Column renderer={renderCheck} width={50} />
        <Column name="name" renderer={renderName} width={400} />
        <Column renderer={renderAction} width={50} />
        <Column name="route" />
        <Column renderer={renderCount} header="勾选权限" />
      </Table>
    );
  }

  function handleChangeTab(key) {
    setTabLevel(key);
  }

  function renderTab() {
    return (
      <React.Fragment>
        <div>
          <span style={{ marginRight: 80, fontSize: '16px' }}>菜单分配</span>
        </div>
        <Tabs onChange={handleChangeTab} activeKey={tabLevel}>
          {getTabCodes().map((l) => (
            <TabPane tab={LEVEL_NAME[l]} key={l}>
              {renderTable(l)}
            </TabPane>
          ))}
        </Tabs>
      </React.Fragment>
    );
  }

  return (
    <React.Fragment>
      {renderForm()}
      {renderLabel()}
      {renderTab()}
    </React.Fragment>
  );
};

export default observer(ListView);
