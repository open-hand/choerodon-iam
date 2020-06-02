import React, { Component, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { observer } from 'mobx-react-lite';
import { FormattedMessage, injectIntl } from 'react-intl';
import classnames from 'classnames';
import { Table } from 'choerodon-ui/pro';
import { Content, Header, Page, Breadcrumb } from '@choerodon/boot';
import { Table as OldTable, Button, Tooltip } from 'choerodon-ui';
import './PermissionInfo.less';
import MouseOverWrapper from '../../../components/mouseOverWrapper';
import { useStore } from './stores';


const { Column } = Table;
function PermissionInfo(props) {
  const context = useStore();
  const { permissionInfoDataSet, AppState, intlPrefix, history } = context;
  const { MenuStore } = context;

  const renderRoleColumn = ({ value }) => (value ? value.map(({ name, enabled }, index) => {
    let item = (
      // eslint-disable-next-line react/no-array-index-key
      <span className={classnames('role-wrapper', { 'role-wrapper-enabled': enabled, 'role-wrapper-disabled': !enabled })} key={index}>
        {name}
      </span>
    );
    if (enabled === false) {
      item = (
        <Tooltip title={<FormattedMessage id={`${intlPrefix}.role.disabled.tip`} />}>
          {item}
        </Tooltip>
      );
    }
    return item;
  }) : '');
  function getFristPath(subMenu) {
    // let i = 0;
    while (subMenu[0].subMenus) {
      subMenu = subMenu[0].subMenus;
    }
    return subMenu[0].route;
  }
  // 缺少项目层

  function getRedirectURL({ id, name, level, projName, tenantId: organizationId }) {
    // console.log(MenuStore);
    switch (level) {
      case 'site':
        return `?organizationId=${AppState.currentMenuType.orgId}`;
      case 'organization':
        return `?type=${level}&organizationId=${id}&id=${id}&name=${encodeURIComponent(name)}`;
      case 'project':
        return `?type=${level}&organizationId=${organizationId}&id=${id}&name=${encodeURIComponent(projName)}`;
      default:
        return { pathname: '/', query: {} };
    }
  }
  const handleLinkTo = (record) => {
    MenuStore.loadMenuData({ type: record.get('level'), id: record.get('id') }, false).then((data) => {
      let path = getFristPath(data[0].subMenus);
      if (record.get('level') === 'organization') {
        path = '/projects';
      }
      history.push(path + getRedirectURL(record.data));
    });
  };

  const renderName = ({ value, record }) => {
    const imageUrl = record.get('imageUrl');
    const projName = record.get('projName');
    const level = record.get('level');
    const siteInfo = AppState.getSiteInfo;
    return (
      <span className="c7n-permission-info-name-link" onClick={handleLinkTo.bind(this, record)}>
        {
          level !== 'site' ? (
            <div className="c7n-permission-info-name-avatar">
              {
                imageUrl ? <img src={imageUrl} alt="avatar" style={{ width: '100%' }} />
                  : <React.Fragment>{projName ? projName.split('')[0] : value.split('')[0]}</React.Fragment>
              }
            </div>
          ) : (
              // eslint-disable-next-line react/jsx-indent
              <div className="c7n-permission-info-name-avatar-default" style={siteInfo.favicon ? { backgroundImage: `url(${siteInfo.favicon})` } : {}} />
              // eslint-disable-next-line indent
            )
        }
        <MouseOverWrapper width={0.18} text={value}>
          <span className="c7n-permission-info-name-text">{value}</span>
        </MouseOverWrapper>
      </span>
    );
  };
  const renderCode = ({ value }) => (
    <MouseOverWrapper text={value} width={0.08}>
      {value}
    </MouseOverWrapper>
  );
  const renderLevel = ({ value }) => (value ? (
    <MouseOverWrapper text={value ? <FormattedMessage id={value} /> : ''} width={0.06}>
      <FormattedMessage id={value} />
    </MouseOverWrapper>
  ) : '');
  function render() {
    const { intl } = context;

    return (
      <Page
        className="c7n-permission-info"
      >
        <Breadcrumb />
        <Content className="c7n-permission-info-content">
          <Table dataSet={permissionInfoDataSet}>
            <Column name="name" width={300} className="c7n-permission-info-name" renderer={renderName} />
            <Column name="code" width={120} renderer={renderCode} className="c7n-permission-info-code" />
            <Column name="level" width={60} renderer={renderLevel} className="c7n-permission-info-level" />
            <Column name="roles" renderer={renderRoleColumn} className="c7n-permission-info-description" />

          </Table>
        </Content>
      </Page>
    );
  }
  return render();
}
export default observer(PermissionInfo);
