import React from 'react';
import { observer } from 'mobx-react-lite';
import { Link } from 'react-router-dom';
import { Breadcrumb, Content } from '@choerodon/boot';
import { Spin } from 'choerodon-ui/pro';
import defaultSvg from './asset/default.svg';
import tokenSvg from './asset/token.svg';
import './NoPermissionPage.less';

const cssPrefix = 'c7n-market-noPermission';

const NoPermissionPage = observer(({ appReleasedDataSet = [], currentPermission, ShowDefaultPage, breadcrumbTitle = null, withoutPermission, children }) => {
  if (!currentPermission) {
    return (
      <Content style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
        <Spin />
      </Content>
    );
  }
  // 用户传入
  // showDeftaultPage, configurationValid, tokenValid, publishPermissionValid;
  // Token 失效 || 未配置 Token || 租户失效且无数据显示缺省页
  // configurationValid: true
  // publishingPermissionValid: true
  // tokenValid: true
  // updateSuccessFlag: true
  const { configurationValid = null, tokenValid = null, publishingPermissionValid = null, enabled = null, generated = null } = currentPermission.toData();
  const toggleMode = () => {
    if (typeof configurationValid === 'boolean' && !configurationValid) {
      return {
        svg: tokenSvg,
        content: (
          <React.Fragment>
            <h4>您当前平台尚未配置远程连接或远程连接已断开</h4>
            <h5>请联系平台管理员<Link to="/iam/safe/token">配置远程连接</Link>，以便查看市场发布列表</h5>
          </React.Fragment>
        ),
      };
    }
    if (typeof tokenValid === 'boolean' && !tokenValid) {
      return {
        svg: tokenSvg,
        content: (
          <React.Fragment>
            <h4>您当前平台远程连接失败</h4>
            <h5>请联系平台管理员，以便查看市场发布列表</h5>
          </React.Fragment>
        ),
      };
    }
    if (typeof publishingPermissionValid === 'boolean' && !publishingPermissionValid && appReleasedDataSet.length === 0) {
      //
      return {
        svg: defaultSvg,
        content: (
          <React.Fragment>
            <h4>您当前租户处于失效状态或者尚未拥有发布应用到choerodon公开应用市场的权限</h4>
            <h5>如需发布应用，请联系choerodon以获得相关权限</h5>
            <p><a href="http://choerodon.io/zh/">http://choerodon.io/zh/</a></p>
          </React.Fragment>
        ),
      };
    }
    if (typeof enabled === 'boolean' && !enabled) {
      return {
        svg: tokenSvg,
        content: (
          <React.Fragment>
            <h4>您的SaaS租户已被停用远程连接</h4>
            <h5>请联系<a href="http://choerodon.io/zh/">http://choerodon.io/zh/</a>猪齿鱼启用远程连接</h5>
          </React.Fragment>
        ),
      };
    }
    if (typeof generated === 'boolean' && !generated) {
      return {
        svg: tokenSvg,
        content: (
          <React.Fragment>
            <h5>您当前SaaS组织尚未创建Token，请先创建Token</h5>
          </React.Fragment>
        ),
      };
    }
    if (JSON.stringify(currentPermission.toData()) === '{}') {
      return {
        svg: tokenSvg,
        content: (
          <React.Fragment>
            <h5>您当前Pass平台尚未配置过Token，请先配置远程连接</h5>
          </React.Fragment>
        ),
      };
    }
    return {
      svg: null,
      content: <p>出现了意料之外的情况</p>,
    };
  };
  if (ShowDefaultPage) {
    return (
      <Content>
        <div className={cssPrefix}>
          <img src={toggleMode().svg} alt="no permission" className={`${cssPrefix}-left`} />
          <div className={`${cssPrefix}-right`}>
            {toggleMode().content}
          </div>
        </div>
      </Content>
    );
  }
  // 租户失效，存在数据时需要禁用所有操作按钮
  return children;
});

export default NoPermissionPage;
