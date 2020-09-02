import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Select, Tooltip } from 'choerodon-ui/pro';
import _ from 'lodash';
import Store from './stores';
import UserOptionDataSet from './stores/UserOptionDataSet';
import FormSelectEditor from '../../../../components/formSelectEditor';
import TwoFormSelectEditor from '../../../../components/twoFormSelectEditor';

import './index.less';

export default observer(() => {
  const {
    prefixCls, modal, onOk, dsStore, roleAssignDataSet, orgRoleDataSet,
  } = useContext(Store);
  useEffect(() => {
    if (roleAssignDataSet.length === 0) { roleAssignDataSet.create({ memberId: [''], roleId: [''] }); }
  });
  function handleCancel() {
    roleAssignDataSet.reset();
  }
  async function handleOk() {
    if (!roleAssignDataSet.current.dirty) {
      return true;
    }
    if (await roleAssignDataSet.submit()) {
      await roleAssignDataSet.reset();
      await onOk();
    } else {
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
    const isLdap = record.get('ldap');
    const email = record.get('email');
    const imgUrl = record.get('imageUrl');
    const realName = record.get('realName');
    const loginName = record.get('loginName');
    return (
      <Tooltip placement="left" title={`${email}`}>
        <div className={`${prefixCls}-option`}>
          <div className={`${prefixCls}-option-avatar`}>
            {
              imgUrl ? <img src={imgUrl} alt="userAvatar" style={{ width: '100%' }} />
                : <span className={`${prefixCls}-option-avatar-noavatar`}>{realName && realName.split('')[0]}</span>
            }
          </div>
          <span>{realName}</span>
          {isLdap && loginName ? (
            <span>
              {`(${loginName})`}
            </span>
          ) : (
            <span>
              {`(${email})`}
            </span>
          )}
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
        optionDataSetConfig={[UserOptionDataSet(), undefined]}
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
