import React from 'react';
import { withRouter } from 'react-router-dom';
import { Button } from 'choerodon-ui/pro';
import { Page, Content, Breadcrumb } from '@choerodon/boot';
import { useHzeroPageStore } from './stores';

import './index.less';

const HzeroPage = withRouter(((props) => {
  const {
    prefixCls,
    intlPrefix,
    history,
    location: { search },
    pathname,
    intl: { formatMessage },
    pageType,
    onClick,
  } = useHzeroPageStore();

  function handleClick() {
    // history.push({
    //   pathname,
    //   search,
    // });
  }

  return (
    <Page>
      <Breadcrumb />
      <Content className={`${prefixCls}`}>
        <div className={`${prefixCls}-wrap`}>
          <div className={`${prefixCls}-image`} />
          <div className={`${prefixCls}-text`}>
            <div className={`${prefixCls}-title`}>
              {formatMessage({ id: `${intlPrefix}.${pageType}.title` })}
            </div>
            <div className={`${prefixCls}-des`}>
              {formatMessage({ id: `${intlPrefix}.${pageType}.describe` })}
            </div>
            <Button
              color="primary"
              onClick={onClick || handleClick}
              funcType="raised"
            >
              {formatMessage({ id: `${intlPrefix}.link` })}
            </Button>
          </div>
        </div>
      </Content>
    </Page>
  );
}));

export default HzeroPage;
