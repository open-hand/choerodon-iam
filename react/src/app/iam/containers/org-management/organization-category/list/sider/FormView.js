import React, { useState, useEffect, useMemo, useCallback, useRef, useContext } from 'react';
import remove from 'lodash/remove';
import { observer } from 'mobx-react-lite';
import { Tabs, Icon, message, Checkbox, Popover } from 'choerodon-ui';
import { Table, Form, TextField, DataSet, Modal, Button, Select, TextArea, Output } from 'choerodon-ui/pro';
import PermissionListView from './PermissionListView';
import Store from './stores';
import '../index.less';

const { Column } = Table;
const { TabPane } = Tabs;

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

const ListView = () => {
  const {
    listDataSet: dataSet,
    organizationFormListDataSet: orgDs,
    projectFormListDataSet: projectDs,
    menuCodeDataSet,
  } = useContext(Store);

  const [tabLevel, setTabLevel] = useState('organization');
  const [permissionsArr, setPermissionsArr] = useState([]);
  const edit = useRef(true);
  const modalPermission = useRef(null);

  function renderForm() {
    return (
      <Form labelAlign="left" style={{ width: '3.5rem' }} record={dataSet.current} columns={1} className="c7n-role-msg-form" labelLayout="horizontal">
        <Output name="code" />
        <Output name="name" />
        <Output name="description" />
        {/* <TextField name="code" style={{ width: '2.48rem', marginRight: '.16rem' }} disabled />
        <TextField name="name" style={{ width: '2.48rem' }} disabled />
        <TextArea resize="vertical" name="description" style={{ width: '5.12rem' }} disabled /> */}
      </Form>
    );
  }

  const handleOk = useCallback((selectedPermissionsArr) => {
    if (!edit.current) {
      return;
    }
    const { current } = orgDs;
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
      okCancel: false,
      className: 'org-category',
      title: (
        <div style={{ display: 'flex', alignItems: 'center' }} onClick={() => modalPermission.current.close()}>
          <Button shape="circle" funcType="flat" icon="arrow_back" style={{ marginRight: 10 }} />
          权限配置
        </div>
      ),
      children: (
        <PermissionListView record={record} onOk={handleOk} />
      ),
      style: modalStyle,
    });
  }

  function renderCount({ record }) {
    if (record.children && record.children.length) {
      return null;
    }
    const recordPermissions = record.data.permissions;
    const filteredRecordPermissions = recordPermissions.filter((p) => p.permissionType !== 'page');
    return `${filteredRecordPermissions.length}`;
  }

  function renderName({ record }) {
    const { icon, name, subMenus } = record.toData();
    if (subMenus) {
      return (
        <div>
          <Icon type={icon} style={{ marginRight: '.08rem', lineHeight: '.32rem', verticalAlign: 'top' }} />
          <span style={{ color: 'rgba(0, 0, 0, 0.65)' }}>{name}</span>
        </div>
      );
    }
    return (
      <span className="link" onClick={() => handleClickRecord(record)}>
        <Icon type={icon} style={{ marginRight: '.08rem', lineHeight: '.32rem', verticalAlign: 'top' }} />
        {name}
      </span>
    );
  }

  function filterRecord({ data }) {
    if (data.subMenus) {
      return true;
    }
    if (tabLevel === 'organization') {
      return menuCodeDataSet.current && menuCodeDataSet.current.get('menuOrgCodes').some(({ menuCode }) => menuCode === data.code);
    } else {
      return menuCodeDataSet.current && menuCodeDataSet.current.get('menuProCodes').some(({ menuCode }) => menuCode === data.code);
    }
  }

  function renderTable() {
    return (
      <Table
        dataSet={tabLevel !== 'organization' ? projectDs : orgDs}
        queryBar="none"
        mode="tree"
        filter={filterRecord}
        buttons={['expandAll', 'collapseAll']}

      >
        {/* <Column renderer={renderCheck} width={50} /> */}
        <Column name="name" renderer={renderName} width={400} />
        <Column className="text-gray" name="route" />
        <Column className="text-gray" renderer={renderCount} header="勾选权限" />
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
          {['organization', 'project'].map((l) => (
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
      {renderTab()}
    </React.Fragment>
  );
};

export default observer(ListView);
