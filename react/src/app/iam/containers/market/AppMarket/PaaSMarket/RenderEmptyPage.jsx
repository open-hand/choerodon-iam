import React, { useContext } from 'react';
import './MainPage.less';
import { Icon } from 'choerodon-ui';
import { TextField } from 'choerodon-ui/pro';
import emptyImg from './images/paas_market_empty_img.svg';
import Store from './Store';

const RenderEmptyPage = (props) => {
  const context = useContext(Store);

  // 搜索事件
  const search = (value) => {
    window.console.log('欢迎来到Choerodon应用市场！');
  };

  // 应用卡片点击 ， 查看应用详情事件
  const changePage = () => {
    context.history.push('/iam/safe/token');
  };

  const searchInput = (
    <TextField
      className="search-input"
      placeholder="搜索应用名称或类型"
      prefix={<Icon className="search-icon" type="search" />}
      onChange={search}
      labelLayout="placeholder"
    />
  );

  return (
    <div className="app-market-main-page">
      {/* <Breadcrumb style={{display: 'none'}} title="应用市场" /> */}
      <div className="main-banner">
        <div className="main-banner-info-1">欢迎来到Choerodon应用市场</div>
        <div className="main-banner-info-2">发现、部署、下载您需要的应用程序，促进业务发展</div>
        <div className="main-banner-info-3">
          {searchInput}
        </div>
      </div>
      <div className="empty-main-content">
        {/* eslint-disable-next-line jsx-a11y/alt-text */}
        <img className="empty-main-content-img" src={emptyImg} />
        <div className="empty-main-content-info">
          <p className="empty-main-content-info-1">您未配置远程token或者现有token已失效</p>
          <p className="empty-main-content-info-2">请联系平台管理员<a onClick={changePage}>配置远程连接</a>，以便查看应用市场</p>
        </div>
      </div>
    </div>
  );
};

export default RenderEmptyPage;
