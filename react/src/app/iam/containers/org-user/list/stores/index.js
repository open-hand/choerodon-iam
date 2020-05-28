import React, { createContext, useEffect, useMemo } from 'react';
import { DataSet } from 'choerodon-ui/pro';
import { inject } from 'mobx-react';
import { injectIntl } from 'react-intl';
import OrgUserListDataSet from './OrgUserListDataSet';
import OrgRoleDataSet from './OrgRoleDataSet';
import OrgUserCreateDataSet from './OrgUserCreateDataSet';
import OrgUserRoleDataSet from './OrgUserRoleDataSet';
import PasswordPolicyDataSet from '../../../safe/org-safe/store/PasswordPolicyDataSet';
import OrgAllRoleDataSet from './OrgAllRoleDataSet';
import useStore from './useStore';

const Store = createContext();

export default Store;

export const StoreProvider = injectIntl(inject('AppState')(
  (props) => {
    const { AppState: { currentMenuType: { type, id, organizationId } }, intl, children } = props;
    const intlPrefix = 'organization.user';
    const statusOptionData = [
      { text: '启用', value: 'true' },
      { text: '停用', value: 'false' },
    ];
    const statusOptionDs = useMemo(() => new DataSet({
      data: statusOptionData,
      selection: 'single',
    }));
    const safeOptionData = [
      { text: '正常', value: 'false' },
      { text: '锁定', value: 'true' },
    ];
    const safeOptionDs = useMemo(() => new DataSet({
      data: safeOptionData,
      selection: 'single',
    }));
    const userStore = useStore();

    const orgRoleDataSet = useMemo(() => new DataSet(OrgRoleDataSet({ id, intl, intlPrefix })), [id]);
    const orgUserListDataSet = useMemo(() => new DataSet(OrgUserListDataSet({ id, intl, intlPrefix, statusOptionDs, safeOptionDs, orgRoleDataSet })), [id]);
    const orgUserCreateDataSet = useMemo(() => new DataSet(OrgUserCreateDataSet({ id, intl, intlPrefix, orgRoleDataSet, userStore })), [id]);
    const orgUserRoleDataSet = useMemo(() => new DataSet(OrgUserRoleDataSet({ id, intl, intlPrefix, orgRoleDataSet })), [id]);
    const passwordPolicyDataSet = useMemo(() => new DataSet(PasswordPolicyDataSet(id, id, intl, intlPrefix)), [id]);
    const orgAllRoleDataSet = useMemo(() => new DataSet(OrgAllRoleDataSet({ id, intl })), [id]);

    useEffect(() => {
      userStore.checkCreate(organizationId);
    }, [organizationId]);

    const value = {
      ...props,
      orgUserListDataSet,
      orgUserCreateDataSet,
      orgUserRoleDataSet,
      orgRoleDataSet,
      orgAllRoleDataSet,
      prefixCls: 'base-org-user-list',
      intlPrefix,
      permissions: [
        'choerodon.code.organization.manager.user.ps.default',
      ],
      organizationId,
      passwordPolicyDataSet,
      userStore,
    };
    return (
      <Store.Provider value={value}>
        {children}
      </Store.Provider>
    );
  },
));
