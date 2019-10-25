import React, { useState, useContext, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button } from 'choerodon-ui';
import { Content, Header, Page, Breadcrumb, Choerodon } from '@choerodon/boot';
import { FormattedMessage } from 'react-intl';
import ApplicationSettingContext, { ContextProvider } from './stores';
import './ApplicationSetting.less';
import '../../common/ConfirmModal.scss';
import Edit from './components/edit';

const ApplicationSetting = observer(() => {
  const { store, AppState, intl: { formatMessage }, intlPrefix, prefixCls } = useContext(ApplicationSettingContext);
  const [editing, setEditing] = useState(false);


  const loadApplication = () => {
    const { id } = AppState.currentMenuType;
    store.axiosGetApplicationInfo(id).then((data) => {
      store.setImageUrl(data.imageUrl);
      store.setApplicationInfo(data);
    }).catch(Choerodon.handleResponseError);
  };


  useEffect(() => {
    loadApplication();
    return () => {
      store.setApplicationInfo({});
    };
  }, []);

  const handleEditClick = () => {
    setEditing(true);
  };

  const handleCancel = () => {
    setEditing(false);
  };

  const { type, name, code, sourceName, creationDate, realName } = store.getApplicationInfo;
  const renderSource = () => {
    switch (type) {
      case 'custom':
        return '新建';
      case 'template':
        return sourceName;
      case 'mkt_code_only':
        return '应用市场';
      case 'mkt_deploy_only':
        return '应用市场';
      case 'mkt_code_deploy':
        return '应用市场';
      default: return '';
    }
  };
  const imageUrl = store.getImageUrl;
  return (
    <Page
      service={[
        'base-service.project.query',
        'base-service.project.update',
        'base-service.project.disableApplication',
        'base-service.project.list',
      ]}
    >
      <Header>
        <Button
          icon="mode_edit"
          onClick={handleEditClick}
        >
          <FormattedMessage id={`${intlPrefix}.modify`} />
        </Button>
      </Header>
      <Breadcrumb />
      <Content style={{ paddingTop: 0 }}>
        <div className={prefixCls}>
          <div style={{ display: 'flex' }}>
            <section className={`${prefixCls}-section`}>
              {/* <div className={`${prefixCls}-section-title`}>
                {formatMessage({ id: `${intlPrefix}.title` })}
              </div> */}
              <div className={`${prefixCls}-section-content`}>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.name` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    {name}
                  </div>
                </div>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.code` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    {code}
                  </div>
                </div>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.source` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    {renderSource()}
                  </div>
                </div>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.creationDate` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    {creationDate}
                  </div>
                </div>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.creator` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    {realName}
                  </div>
                </div>
              </div>
            </section>
            <section className={`${prefixCls}-section`} style={{ marginLeft: 100 }}>
              <div className={`${prefixCls}-section-title`}>&nbsp;</div>
              <div className={`${prefixCls}-section-content`}>
                <div className={`${prefixCls}-section-item`}>
                  <div className={`${prefixCls}-section-item-title`}>
                    {formatMessage({ id: `${intlPrefix}.avatar` })}:
                  </div>
                  <div className={`${prefixCls}-section-item-content`}>
                    <div className="c7n-iam-ApplicationSetting-avatar">
                      <div
                        className="c7n-iam-ApplicationSetting-avatar-wrap"
                        style={{
                          backgroundColor: '#c5cbe8',
                          backgroundImage: imageUrl ? `url(${Choerodon.fileServer(imageUrl)})` : '',
                        }}
                      >
                        {!imageUrl && name && name.charAt(0)}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </div>
        </div>
        <Edit
          visible={editing}
          onCancel={handleCancel}
        />
      </Content>
    </Page>
  );
});


export default function Index(props) {
  return (
    <ContextProvider {...props}>
      <ApplicationSetting />
    </ContextProvider>
  );
}
