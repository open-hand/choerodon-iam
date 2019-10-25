package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import org.springframework.data.domain.Pageable;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/5
 */
public interface RemoteTokenAuthorizationService {

    /**
     * 存储远程连接令牌并校验是否有效
     *
     * @param tokenBase64
     * @return
     */
    RemoteTokenAuthorizationVO storeAndCheckToken(String tokenBase64);

    /**
     * 校验最新token是否有效
     *
     * @return
     */
    RemoteTokenAuthorizationVO checkLatestToken();

    /**
     * 分页查询校验远程TOKEN的历史记录
     *
     * @param name
     * @param email
     * @param status
     * @param organizationName
     * @param params
     * @param Pageable
     * @return
     */
    PageInfo<RemoteTokenAuthorizationVO> pagingAuthorizations(String name, String email, String status,
                                                              String organizationName, String[] params, Pageable Pageable);


    /**
     * 断开token连接
     *
     * @return
     */
    RemoteTokenAuthorizationVO breakLatestToken();

    /**
     * token重新连接
     *
     * @return
     */
    RemoteTokenAuthorizationVO reconnectLatestToken();

}
