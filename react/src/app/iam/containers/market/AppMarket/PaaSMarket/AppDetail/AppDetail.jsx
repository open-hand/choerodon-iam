import React, { useContext, useState, useEffect, useCallback, useRef } from 'react';
import { observer } from 'mobx-react-lite';
import { Link } from 'react-router-dom';
import { Content, Header, Page, Action, axios, Breadcrumb } from '@choerodon/boot';
import { DataSet, Spin, Tooltip, Table, TextField, Form, Modal, Button, Select } from 'choerodon-ui/pro';
import { Badge, Collapse, Breadcrumb as Bread, message, Tabs, Icon, Select as Select2 } from 'choerodon-ui';
import ReactMarkdown from 'react-markdown';
import './AppDetail.less';
import Record from 'choerodon-ui/pro/lib/data-set/Record';
import Store from './Store';
import AppDownloadVersionDataSet from './Store/AppDownloadVersionDataSet';
import ServiceListDataSet from './Store/ServiceListDataSet';

const { TabPane } = Tabs;
const { Option } = Select;
const Option2 = Select2.Option;
const { Column } = Table;
const { Item } = Bread;
const { Panel } = Collapse;
const serviceListDataSetArr = [];

const AppDetail = (props) => {
  const context = useContext(Store);
  const { appDetailDataSet, appVersionDataSet, appVersionStoreDataSet, appAllVersionDataSet, history } = context;
  let appDownloadVersionDataSet = null;
  const { orgId } = context.AppState.menuType;

  const [tabKey, setTabKey] = useState(1);
  const versionSelected = useRef(null);

  const addResultToDisplay = (add) => {
    if (add) {
      appVersionStoreDataSet.push(...appVersionDataSet.all);
    } else {
      appVersionStoreDataSet.reset();
      appVersionStoreDataSet.push(...appVersionDataSet.all);
    }
  };

  useEffect(() => {
    appVersionDataSet.query().then((res) => {
      if (res.failed) {
        return message.error(res.message);
      }
      addResultToDisplay(false);
    });
  }, []);

  const callback = (key) => {
    setTabKey(key);
  };

  const handleChange2 = (value, oldValue) => {
    versionSelected.current = value;
  };

  const selectOptionDisabled = (item) => {
    // 未购买/空/下载中/已下载/下载失败/待更新/更新失败
    if (item.displayStatus === 'not_purchased') {
      return true;
    } else if (item.displayStatus === 'not_downloaded') {
      return false;
    } else if (item.displayStatus === 'downloading') {
      return true;
    } else if (item.displayStatus === 'completed') {
      return true;
    } else if (item.displayStatus === 'download_failed') {
      return false;
    } else if (item.displayStatus === 'upgrade') {
      return false;
    } else if (item.displayStatus === 'update_failed') {
      return false;
    }
  };

  const rendererVersionStatus = (item) => {
    // 未购买/空/下载中/已下载/下载失败/待更新/更新失败
    if (item.displayStatus === 'not_purchased') {
      return <span className="version-status unpurchased">未购买</span>;
    } else if (item.displayStatus === 'not_downloaded') {
      return null;
    } else if (item.displayStatus === 'downloading') {
      return <span className="version-status downloading">下载中</span>;
    } else if (item.displayStatus === 'completed') {
      return <span className="version-status downloaded">已下载</span>;
    } else if (item.displayStatus === 'download_failed') {
      return <span className="version-status failed">下载失败</span>;
    } else if (item.displayStatus === 'upgrade') {
      return <span className="version-status upgrade">待更新</span>;
    } else if (item.displayStatus === 'update_failed') {
      return <span className="version-status upgrade-failed">更新失败</span>;
    }
  };

  const DownloadView = observer(() => {
    if (!appDownloadVersionDataSet.current) {
      return '暂无版本';
    }
    return (
      <Select2
        className="app-detail-download-select"
        label="应用版本"
        style={{ width: '100%' }}
        onChange={handleChange2}
        defaultValue={!appDetailDataSet.current.get('enableDownload') ? null : (appDownloadVersionDataSet.get(0).get('id'))}
      >
        {
          appDownloadVersionDataSet.map((version) => <Option2 className="app-detail-download-select-option" disabled={selectOptionDisabled(version.toData())} value={version.get('id')}><Tooltip title={version.get('displayStatus') === 'not_purchased' && '您当前未购买该应用版本，如需下载，请联系Choerodon购买'}>{version.get('version')}{rendererVersionStatus(version.toData())}</Tooltip></Option2>)
        }
      </Select2>
    );
  });

  const download = () => {
    Modal.open({
      key: Modal.key(),
      title: `下载 “${appDetailDataSet.current.get('name')}” 应用`,
      children: (<DownloadView />),
      footer: (okBtn, cancelBtn) => (
        <div>
          {cancelBtn}
          {appDetailDataSet.current.get('enableDownload') ? okBtn : <Tooltip title="暂无可下载版本！"><div style={{ display: 'inline-block' }}>{okBtn}</div></Tooltip>}
        </div>
      ),
      okText: '下载',
      okProps: { disabled: !appDetailDataSet.current.get('enableDownload') },
      onOk: () => axios.post(`/base/v1/applications/${versionSelected.current || appDownloadVersionDataSet.get(0).get('id')}/download?organization_id=${orgId}`)
        .then((res) => {
          // console.log(res);
          if (!res.failed) {
            message.success('开始下载！');
            return true;
          } else {
            message.error(res.message);
            return false;
          }
        })
        .catch((err) => {
          // console.log(err);
          message.error(err.message);
          return false;
        }),
      afterClose: () => {
        appVersionDataSet.reset();
        appVersionDataSet.currentPage = 1;
        appVersionStoreDataSet.query();
        appDetailDataSet.query();
        appDownloadVersionDataSet.query();
      },
    });
  };

  const handleChange = (value, oldValue) => {
    // 发请求获取其他版本的文档
    versionSelected.current = value;
    appDetailDataSet.queryDataSet.current.set('version_id', value);
    appDetailDataSet.query();
  };

  const renderDownloadType = (type) => {
    if (type === 'mkt_code_deploy') {
      return (
        <span><span>源代码<span className="point">·</span></span><span>部署包<span className="point">·</span></span></span>
      );
    } else if (type === 'mkt_code_only') {
      return (
        <span>源代码<span className="point">·</span></span>
      );
    } else if (type === 'mkt_deploy_only') {
      return (
        <span>部署包<span className="point">·</span></span>
      );
    }
  };

  const svcVersionContainer = ({ value, record }) => value.map((item) => (
    // <Tooltip title={item.version}>
    <Badge dot={item.newFixVersion} offset={[2, -2]}>
      <p className="version-tag">{item.version}</p>
    </Badge>
    // </Tooltip>
  ));

  if (!appDetailDataSet.current) {
    return (
      <Page>
        <Header>
          <Button
            className="app-detail-header-btn"
            icon="get_app"
            funcType="flat"
            color="primary"
            disabled
          >
            下载应用
          </Button>
          {/* <Button
            className="app-detail-header-btn"
            icon="refresh"
            funcType="flat"
            color="primary"
          >
            刷新
          </Button> */}
        </Header>
        <Breadcrumb custom>
          <Item>
            <Link to={`/base/app-market${history.location.search}`}>应用市场</Link>
          </Item>
          <Item style={{ color: 'rgba(0, 0, 0, 0.87)' }}>应用详情</Item>
        </Breadcrumb>
        <Content>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <Spin />
          </div>
        </Content>
      </Page>
    );
  } else {
    appDownloadVersionDataSet = new DataSet(AppDownloadVersionDataSet(appDetailDataSet.current.get('code'), orgId));
  }

  const refresh = () => {
    appDetailDataSet.query();
    appVersionDataSet.query();
    appAllVersionDataSet.query();
  };

  const turnToMainPage = (categoryId) => {
    context.history.push(`/base/app-market/category/${categoryId}`);
  };

  const getMoreVersion = () => {
    appVersionDataSet.query(appVersionDataSet.currentPage + 1).then((res) => {
      if (res.failed) {
        return message.error(res.message);
      }
      addResultToDisplay(true);
    });
  };

  return (
    <Page>
      <Header>
        <Button
          className="app-detail-header-btn"
          icon="get_app"
          funcType="flat"
          color="primary"
          onClick={download}
        >
          下载应用
        </Button>
      </Header>
      <Breadcrumb custom>
        <Item>
          <Link to={`/base/app-market${history.location.search}`}>应用市场</Link>
        </Item>
        <Item>应用详情</Item>
      </Breadcrumb>
      <Content>
        <Spin spinning={appDetailDataSet.status === 'loading'}>
          <div>
            <div className="app-detail-info">
              <div className="app-detail-info-img">
                <div className="app-detail-info-img-wrap">
                  <img src={appDetailDataSet.current.get('imageUrl')} alt="detailImg" />
                </div>
              </div>
              <div className="app-detail-info-content">
                <div className="name">
                  <span className="title">{appDetailDataSet.current.get('name')}</span>
                  <span className={appDetailDataSet.current.get('free') ? 'free' : 'charge'}>
                    {
                      appDetailDataSet.current.get('free') ? '免费' : '付费'
                    }
                  </span>
                </div>
                <div className="info">
                  {renderDownloadType(appDetailDataSet.current.get('type'))}
                  {appDetailDataSet.current.get('contributorUrl') ? <img className="name-img" alt="nameImg" src={appDetailDataSet.current.get('contributorUrl')} /> : <div className="name-avatar">{appDetailDataSet.current.get('contributor').split('')[0]}</div>}
                  {appDetailDataSet.current.get('contributor')}<span className="point">·</span>
                  <Icon type="get_app" /><span className="download-count">{appDetailDataSet.current.get('downCount') ? appDetailDataSet.current.get('downCount') : 0}</span>
                </div>
                <div className="description">{appDetailDataSet.current.get('description')}</div>
              </div>
            </div>
            <div className="app-detail-description">
              <div className="app-detail-description-tabs">
                <Tabs defaultActiveKey="1" onChange={callback}>
                  <TabPane className="tabk" tab="应用介绍" key="1">
                    <div className="app-detail-tab-content" dangerouslySetInnerHTML={{ __html: `${appDetailDataSet.current.get('overview')}` }} />
                  </TabPane>
                  <TabPane className="tabk" tab="文档" key="2">
                    <div className="app-detail-tab-content">
                      <div className="app-detail-select-doc-version">
                        <Select2
                          label="切换版本"
                          onChange={handleChange}
                          showSearch
                          clearButton={false}
                          defaultValue={appAllVersionDataSet.current.get('id')}
                        >
                          {
                            appAllVersionDataSet.map((version) => <Option2 value={version.get('id')}>{version.get('version')}</Option2>)
                          }
                        </Select2>
                      </div>
                      <div dangerouslySetInnerHTML={{ __html: `${appDetailDataSet.current.get('document')}` }} />
                    </div>
                  </TabPane>
                  <TabPane className="tabk app-detail-description-version-list" tab="所有版本" key="3">
                    {
                      appVersionStoreDataSet.map(
                        (item, i) => {
                          const itemToJS = item.toData();
                          const currentServiceDataSet = new DataSet(ServiceListDataSet());
                          currentServiceDataSet.loadData(item.get('marketAppServiceVOS') || []);
                          return (
                            <div key={itemToJS.id} className="publicVersion">
                              <div className="publicOracle" />
                              <div className="publicTabtitle">
                                <Badge dot={itemToJS.newVersion} offset={[2, -2]}>
                                  <Icon type="versionline" />
                                </Badge>
                                {itemToJS.version}{rendererVersionStatus(itemToJS)}
                              </div>
                              <div className="publishDate">发布时间： {itemToJS.publishDate} <p style={{ display: 'inline-block', padding: '0 0.06rem', margin: 0 }}>·</p> 最近更新时间： {itemToJS.latestFixVersionDate}</div>
                              <div className="services">
                                <Collapse bordered={false}>
                                  <Panel header="所包含的应用服务" key="1">
                                    <div className="services-table">
                                      <Table dataSet={currentServiceDataSet} queryBar="none">
                                        <Column name="name" tooltip="overflow" />
                                        <Column className="market-service-versions" name="serviceVersionVOS" renderer={svcVersionContainer} />
                                      </Table>
                                    </div>
                                  </Panel>
                                </Collapse>
                              </div>
                              <div className="changelog">
                                <div className="changelog-title">changelog</div>
                                <div className="changelog-content">
                                  <ReactMarkdown source={itemToJS.changelog} />
                                </div>
                              </div>
                              <div className="public-Triangle" />
                            </div>
                          );
                        },
                      )
                    }
                    {appVersionDataSet.totalCount > appVersionStoreDataSet.length && (
                      <div className="app-versions-read-more" onClick={() => getMoreVersion()}>
                        <Icon type="expand_more" /><p>查看更多版本</p>
                      </div>
                    )}
                  </TabPane>
                </Tabs>
              </div>
              <div className="app-detail-description-more">
                <div className="title">更多信息</div>
                <div className="info-wrap">
                  <div className="name">发布时间</div>
                  <div className="value">{appDetailDataSet.current.get('publishDate')}</div>
                </div>
                <div className="info-wrap">
                  <div className="name">上次更新时间</div>
                  <div className="value">{appDetailDataSet.current.get('latestVersionDate')}</div>
                </div>
                <div className="info-wrap">
                  <div className="name">最新版本</div>
                  <div className="value">{appDetailDataSet.current.get('latestVersion')}</div>
                </div>
                <div className="info-wrap">
                  <div className="name">分类</div>
                  <div className="value category" onClick={() => turnToMainPage(appDetailDataSet.current.get('categoryId'))}>{appDetailDataSet.current.get('categoryName')}</div>
                </div>
              </div>
            </div>
          </div>
        </Spin>
      </Content>
    </Page>
  );
};

export default observer(AppDetail);
