import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Spin, SelectBox, Password, Select, Tooltip } from 'choerodon-ui/pro';
import _ from 'lodash';
import Store from './stores';
import UserOptionDataSet from './stores/UserOptionDataSet';
import './index.less';
import TwoFormSelectEditor from '../../../../components/twoFormSelectEditor';

export default observer((props) => {
  const { prefixCls, intlPrefix, intl, modal, onOk, dsStore, roleAssignDataSet, projectId, orgRoleDataSet } = useContext(Store);
  useEffect(() => {
    if (roleAssignDataSet.length === 0) { roleAssignDataSet.create({ memberId: [''], roleId: [''] }); }
  });
  function handleCancel() {
    roleAssignDataSet.reset();
  }
  async function handleOk() {
    try {
      await roleAssignDataSet.validate();
      if (await roleAssignDataSet.submit()) {
        await roleAssignDataSet.reset();
        await onOk();
      } else {
        return false;
      }
    } catch (err) {
      return false;
    }
  }

  modal.handleCancel(handleCancel);
  modal.handleOk(handleOk);

  const queryUser = _.debounce((str, optionDataSet) => {
    optionDataSet.setQueryParameter('user_name', str);
    if (str !== '') { optionDataSet.query(); }
  }, 500);
  function handleFilterChange(e, optionDataSet) {
    e.persist();
    queryUser(e.target.value, optionDataSet);
  }
  
  function getOption({ record }) {
    return (
      <Tooltip placement="left" title={`${record.get('email')}`}>
        <div className={`${prefixCls}-option`}>
          <div className={`${prefixCls}-option-avatar`}>
            {
              record.get('imageUrl') ? <img src={record.get('imageUrl')} alt="userAvatar" style={{ width: '100%' }} />
                : <span className={`${prefixCls}-option-avatar-noavatar`}>{record.get('realName') && record.get('realName').split('')[0]}</span>
            }
          </div>
          <span>{record.get('realName')}</span>
          {record.get('ldap') && record.get('loginName') ? (
            <span>({record.get('loginName')})</span>
          ) : null}
        </div>
      </Tooltip>

    );
  }

  return (
    <div
      className={`${prefixCls} ${prefixCls}-modal`}
    >
      <TwoFormSelectEditor
        record={[roleAssignDataSet.current, roleAssignDataSet.current]}
        optionDataSetConfig={[UserOptionDataSet({ id: projectId }), undefined]}
        optionDataSet={[undefined, orgRoleDataSet]}
        name={['memberId', 'roleId']}
        addButton="添加其他用户"
        dsStore={[dsStore]}
      >
        {[(itemProps) => (
          <Select 
            {...itemProps}
            labelLayout="float"
            searchable
            searchMatcher={() => true}
            onInput={(e) => handleFilterChange(e, itemProps.options)}
            style={{ width: '100%' }}
            optionRenderer={getOption}
          />
        ), (itemProps) => (
          <Select 
            {...itemProps}
            labelLayout="float"
            style={{ width: '100%' }}
          />
        )]}
      </TwoFormSelectEditor>
    </div>
  );
});
