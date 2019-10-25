import React, { useState, useEffect } from 'react';
import RenderMainage from './RenderMainPage'
import RenderEmptyPage from './RenderEmptyPage'
import { axios } from '@choerodon/boot';
import { Spin, Icon, BackTop } from 'choerodon-ui';
import { TextField } from 'choerodon-ui/pro';

const MainPage = (props) => {

    const [haveToken, setHaveToken] = useState(0);
    useEffect(() => checkToken(), [])

    //校验token
    const checkToken = () => {
        axios.get("/base/v1/paas_app_market/token")
            .then((record) => {
                if (record && !record.failed) {
                    setHaveToken(1)
                } else {
                    setHaveToken(2)
                }
            })
            .catch((err) => {
                console.log(err)
                setHaveToken(2)
            })
        // axios.get("https://apim.choerodon.com.cn/apps/list", {
        //     headers: {'Apim-Mode': "debug", "AccessToken": "46c02cae-597f-419c-b3ff-be86b1c06826"}
        //   })
    }

    const LoadingPage = () => {

        const searchInput = <TextField
            className="search-input"
            placeholder="搜索应用名称或类型"
            prefix={<Icon className="search-icon" type="search" />}
            labelLayout="placeholder"
        />

        return <div className="app-market-main-page">
            <div className="main-banner">
                <div className="main-banner-info-1">欢迎来到Choerodon应用市场</div>
                <div className="main-banner-info-2">发现、部署、下载您需要的应用程序，促进业务发展</div>
                <div className="main-banner-info-3">
                    {searchInput}
                </div>
            </div>
            <div className="empty-main-content">
                <Spin />
            </div>
        </div>
    }

    return (
        <div style={{ overflow: 'scroll', height: '100%' }} id="app-market-main">
            <BackTop target={() => document.getElementById('app-market-main')} />
            {haveToken === 0 ? <LoadingPage /> : (haveToken === 1 ? <RenderMainage /> : <RenderEmptyPage />)}
        </div>
    )
};

export default MainPage;