import React, { useContext, useState, use } from 'react';
import _ from 'lodash';
import { FormattedMessage, injectIntl } from 'react-intl';
import { observer } from 'mobx-react-lite';
import { Action, Content, axios, Page, Permission, Breadcrumb, TabPage } from '@choerodon/boot';
import { Select, Tooltip } from 'choerodon-ui/pro';
import Store from './stores';
import './index.less';
import FormSelectEditor from '../../../../components/formSelectEditor';
import TwoFormSelectEditor from '../../../../components/twoFormSelectEditor';

export default observer((props) => {
  const { prefixCls, intlPrefix, intl, orgAdminCreateDataSet, OrgUserDataSetConfig, orgAdminListDataSet, modal, dsStore } = useContext(Store);
  modal.handleOk(async () => {
    try {
      const res = await orgAdminCreateDataSet.submit();
      if (!res || res.failed) {
        throw new Error();
      }
      orgAdminListDataSet.query();
      return true;
    } catch (e) {
      return false;
    }
  });

  function getUserOption({ record, text, value }) {
    // label: realName
    // Tooltip: email
    // TextField: imageUrl, realName,
    // valueField: id
    return (
      <Tooltip placement="left" title={`${record.get('email')}`}>
        <div className={`${prefixCls}-option`}>
          <div className={`${prefixCls}-option-avatar`}>
            {
              record.get('imageUrl') ? <img src={record.get('imageUrl')} alt="userAvatar" style={{ width: '100%' }} />
                : <span className={`${prefixCls}-option-avatar-noavatar`}>{record.get('realName') && record.get('realName').split('')[0]}</span>
            }
          </div>
          <span>{text}</span>
        </div>
      </Tooltip>
    );
  }

  const queryUser = _.debounce((str, optionDataSet) => {
    optionDataSet.setQueryParameter('user_name', str);
    if (str !== '') {
      optionDataSet.query();
    }
  }, 500);
  function handleFilterChange(e, optionDataSet) {
    e.persist();
    queryUser(e.target.value, optionDataSet);
  }

  return (
    <div className={prefixCls}>
      <FormSelectEditor
        record={orgAdminCreateDataSet.current}
        optionDataSetConfig={OrgUserDataSetConfig}
        name="userName"
        addButton={intl.formatMessage({ id: 'organization.admin.sider.button.add' })}
        alwaysRequired
        canDeleteAll={false}
        dsStore={dsStore}
      >
        {((itemProps) => (
          <Select
            {...itemProps}
            labelLayout="float"
            style={{ width: '100%' }}
            searchable
            searchMatcher={() => true}
            onInput={(e) => handleFilterChange(e, itemProps.options)}
            optionRenderer={getUserOption}
          />
        ))}
      </FormSelectEditor>
    </div>
  );
});
