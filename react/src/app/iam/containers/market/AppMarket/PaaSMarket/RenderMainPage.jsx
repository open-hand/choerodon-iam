import React, { useContext, useState, useRef, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import './MainPage.less';
import { Spin, Menu, Icon, Badge, message } from 'choerodon-ui';
import { TextField, Table, Tooltip, Button } from 'choerodon-ui/pro';
import { Breadcrumb, axios, StatusTag } from '@choerodon/boot';
import queryString from 'query-string';
import Card from '../../../../components/appMarketCard';
import Store from './Store';
import colorMap from '../../../../utils/colorMap';

const { Item, ItemGroup } = Menu;
const { Column } = Table;

const statusMap = {
  downloading: '下载中',
  completed: '下载成功',
  failed: '下载失败',
};

const RenderMainPage = (props) => {
  const context = useContext(Store);
  const { location, projectId, organizationId } = context;
  const appCategoryDS = context.appCategoryDataSet;
  const marketAppsDS = context.marketAppsDataSet;
  const downloadRecordDS = context.downloadRecordDataSet;
  const { marketAppsStoreDataSet, history } = context;

  const [downloadCount, setDownloadCount] = useState(0);
  const [updateDate, setUpdateDate] = useState(0);
  const [publishDate, setPublishDate] = useState(1);
  const [keys, setKeys] = useState([]);
  const [showRecord, setShowRecord] = useState(false);
  const inputEle = useRef(null);
  const cardContent = useRef(null);
  const [updateCount, setUpdateCount] = useState(0);
  const [queryCount, setQueryCount] = useState(0);

  const getWidth = () => {
    const { clientWidth, clientHeight } = cardContent.current;
    // console.log('width', clientWidth);
    return (parseInt(clientWidth / 224, 10) * 4);
  };

  const addResultToDisplay = (add) => {
    if (add) {
      marketAppsStoreDataSet.push(...marketAppsDS.all);
    } else {
      marketAppsStoreDataSet.reset();
      marketAppsStoreDataSet.push(...marketAppsDS.all);
    }
  };

  const queryApps = (add) => {
    marketAppsDS.query().then((res) => {
      if (res.failed) {
        return message.error(res.message);
      }
      addResultToDisplay(add);
    });
  };

  // 查询下载应用需要更新的数量
  const fetchUpdateCount = () => axios.get(`/iam/choerodon/v1/paas_app_market/newVersionNum?organizationId=${organizationId}`)
    .then((record) => {
      if (!record.failed) {
        setUpdateCount(record);
      } else {
        // console.log(record.message);
      }
    })
    .catch((err) => {
      // console.log(err);
    });

  useEffect(() => {
    appCategoryDS.query();
    setQueryCount(cardContent && cardContent.current && getWidth());
  }, [cardContent]);

  useEffect(() => {
    if (queryCount > 0) {
      marketAppsDS.pageSize = queryCount;
      const pathArrayy = location.pathname.split('/');
      const currentCategory = pathArrayy[pathArrayy.length - 1];
      // console.log(currentCategory);
      if (currentCategory) {
        setKeys([currentCategory]);
        marketAppsDS.queryDataSet.current.set('categoryId', currentCategory);
        queryApps(false);
      } else {
        queryApps(false);
      }
      fetchUpdateCount();
    }
  }, [queryCount]);

  // 渲染应用类型
  const categoryBtn = (record) => <Item key={record.id} title={record.name}>{record.name}</Item>;

  // 应用卡片点击 ， 查看应用详情事件
  const appDetail = (id) => {
    context.history.push(`/iam/app-market/${id}${history.location.search}`);
  };

  const resetMenu = () => {
    setKeys([]);
  };

  // 搜索事件
  const search = (value) => {
    marketAppsDS.queryDataSet.current.reset();
    marketAppsDS.queryDataSet.current.set('param', value);
    queryApps(false);
    resetMenu();
  };

  // 渲染应用卡片
  const appCard = (app) => (
    <div className="main-content-apps-result-card" onClick={() => appDetail(app.id)}>
      <Card
        category={app.category}
        name={app.name}
        img={app.imageUrl}
        contributor={app.contributor}
        description={app.description}
        hasNewVersion={app.hasNewVersion}
        free={app.free}
      />
    </div>
  );

  const resetSort = () => {
    setDownloadCount(0);
    setPublishDate(1);
    setUpdateDate(0);
  };

  const restSearchInput = () => {
    inputEle.current.reset();
  };

  const menuClick = ({ key }) => {
    setKeys([key]);
    // console.log(key)
    marketAppsDS.queryDataSet.current.reset();
    if (key === 'my_download') {
      setShowRecord(false);
      // 查看我的下载应用
      marketAppsDS.queryDataSet.current.set('isMyDownload', true);
      queryApps(false);
    } else if (key === 'download_record') {
      // console.log('下载记录');
      setShowRecord(true);
      downloadRecordDS.query();
    } else if (key === 'all_apps') {
      // 查看全部应用
      setShowRecord(false);
      queryApps(false);
    } else {
      setShowRecord(false);
      marketAppsDS.queryDataSet.current.set('categoryId', key);
      queryApps(false);
    }
    resetSort();
    restSearchInput();
  };

  // 排序字段点击事件
  const clickFilter = (filterName) => {
    if (filterName === 'download_count') {
      if (downloadCount === 1) {
        setDownloadCount(2);
        marketAppsDS.queryDataSet.current.set({
          orderBy: 'downCount',
          order: 'DESC',
        });
      } else {
        setDownloadCount(1);
        setPublishDate(0);
        setUpdateDate(0);
        marketAppsDS.queryDataSet.current.set({
          orderBy: 'downCount',
          order: 'ASC',
        });
      }
    } else if (filterName === 'update_date') {
      setDownloadCount(0);
      setPublishDate(0);
      setUpdateDate(1);
      marketAppsDS.queryDataSet.current.set({
        orderBy: 'latestVersionDate',
        order: 'DESC',
      });
    } else if (filterName === 'publish_date') {
      resetSort();
      marketAppsDS.queryDataSet.current.set({
        orderBy: 'id',
        order: 'DESC',
      });
    }
    queryApps(false);
  };

  const filterClassName = 'main-content-apps-filter';

  const searchInput = (
    <TextField
      className="search-input"
      placeholder="搜索应用名称或类型"
      prefix={<Icon className="search-icon" type="search" />}
      onChange={search}
      labelLayout="placeholder"
      ref={inputEle}
    />
  );

  const renderDownloadStatus = ({ value }) => <StatusTag name={statusMap[value]} color={colorMap(value)} />;

  const renderUser = ({ value, record }) => (
    <div className="downloader-wrap">
      {record.get('downloaderImgUrl') ? <img alt="download" className="downloader-image" src={record.get('downloaderImgUrl')} /> : <div className="downloader-avatar">{value.split('')[0]}</div>}
      <span className="downloader-name">{value}</span>
    </div>
  );

  const renderApp = ({ value, record }) => (
    <div className="application-wrap" onClick={() => appDetail(record.get('mktAppId'))}>
      <img alt="application" className="application-image" src={record.get('mktAppImageUrl')} />
      <span className="application-name">{value}</span>
    </div>
  );

  const downloadRecord = (
    <div className="download-record-table">
      <Table dataSet={downloadRecordDS}>
        <Column name="mktAppName" renderer={renderApp} tooltip="overflow" />
        <Column name="categoryName" style={{ color: 'rgba(0,0,0,0.65)' }} tooltip="overflow" />
        <Column name="mktVersionName" style={{ color: 'rgba(0,0,0,0.65)' }} />
        <Column name="downloaderRealName" renderer={renderUser} style={{ color: 'rgba(0,0,0,0.65)' }} tooltip="overflow" />
        <Column name="creationDate" style={{ color: 'rgba(0,0,0,0.65)' }} />
        <Column name="status" renderer={renderDownloadStatus} />
      </Table>
    </div>
  );

  const readMore = () => {
    marketAppsDS.query(marketAppsDS.currentPage + 1).then((res) => {
      if (res.failed) {
        return message.error(res.message);
      }
      addResultToDisplay(true);
    });
  };

  const appCardList = (
    <div>
      <div className={filterClassName} style={{ width: (queryCount / 4) * 224 - 12 }}>
        <span className={`${filterClassName}-count`}>共 {marketAppsStoreDataSet.length} 结果</span>
        <span className={`${filterClassName}-right`}>
          <p className={`${filterClassName}-sort`}>排序: </p>
          <p
            className={(downloadCount === 1 || downloadCount === 2) ? `${filterClassName}-sort-value-selected ${filterClassName}-sort-value` : `${filterClassName}-sort-value`}
            onClick={() => clickFilter('download_count')}
          >
            下载次数{downloadCount === 1 ? <Icon type="arrow_upward" /> : null}{downloadCount === 2 ? <Icon type="arrow_downward" /> : null}
          </p>
          <p
            className={updateDate === 1 ? `${filterClassName}-sort-value-selected ${filterClassName}-sort-value` : `${filterClassName}-sort-value`}
            onClick={() => clickFilter('update_date')}
          >更新时间
          </p>
          <p
            className={publishDate === 1 ? `${filterClassName}-sort-value-selected ${filterClassName}-sort-value` : `${filterClassName}-sort-value`}
            onClick={() => clickFilter('publish_date')}
          >发布时间
          </p>
        </span>
      </div>
      <Spin spinning={marketAppsDS.status === 'loading'}>
        <div className="main-content-apps-result" ref={cardContent}>
          {marketAppsStoreDataSet.length === 0
            ? <div style={{ paddingLeft: '0.1rem' }}>暂无应用</div>
            : marketAppsStoreDataSet.map(a => appCard(a.toData()))}
        </div>
      </Spin>
      {marketAppsDS.totalCount > marketAppsStoreDataSet.length
        && (
        <div className="main-content-apps-get-more">
          <Button loading={marketAppsDS.status === 'loading'} onClick={() => readMore()} funcType="flat" color="primary">查看更多</Button>
        </div>
        )}
    </div>
  );

  return (
    <div className="app-market-main-page">
      <div className="main-banner">
        <div className="main-banner-info-1">欢迎来到Choerodon应用市场</div>
        <div className="main-banner-info-2">发现、部署、下载您需要的应用程序，促进业务发展</div>
        <div className="main-banner-info-3">
          {searchInput}
        </div>
      </div>
      <div className="main-content">
        <div className="main-content-menu">
          <Menu
            defaultSelectedKeys={['all_apps']}
            onClick={menuClick}
            selectedKeys={keys}
          >
            <ItemGroup key="personal" title="下载中心">
              {updateCount > 0
                ? (
                  <Item className="my-download-item" key="my_download">
                    <Tooltip title={`您有${updateCount}个应用待更新`}>
                      <Badge className="update-count" count={updateCount} offset={[6, 10]}>已下载应用</Badge>
                    </Tooltip>
                  </Item>
                )
                : (
                  <Item className="my-download-item" key="my_download">
                    <Badge className="update-count" count={updateCount} offset={[6, 10]}>已下载应用</Badge>
                  </Item>
                )}
              <Item key="download_record">下载记录</Item>
            </ItemGroup>
            <ItemGroup key="category" title="应用类别">
              <Item key="all_apps">全部应用</Item>
              {appCategoryDS.map(category => categoryBtn(category.toData()))}
            </ItemGroup>
          </Menu>
        </div>
        <div className="main-content-apps">
          {showRecord ? downloadRecord : appCardList}
        </div>
      </div>
    </div>
  );
};

export default observer(RenderMainPage);
