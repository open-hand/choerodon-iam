package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import okhttp3.ResponseBody;
import org.modelmapper.ModelMapper;
import org.modelmapper.internal.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import io.choerodon.base.api.dto.CommonCheckResultVO;
import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.app.service.RemoteTokenAuthorizationService;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.RemoteTokenAuthorizationDTO;
import io.choerodon.base.infra.enums.RemoteTokenCarryType;
import io.choerodon.base.infra.enums.RemoteTokenCheckFailedType;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.mapper.RemoteTokenAuthorizationMapper;
import io.choerodon.base.infra.retrofit.RemoteTokenRetrofitClient;
import io.choerodon.base.infra.utils.RemoteTokenBase64Util;
import io.choerodon.base.infra.utils.RetrofitCallExceptionParse;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.web.util.PageableHelper;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/8/5
 */
@Service
public class RemoteTokenAuthorizationServiceImpl implements RemoteTokenAuthorizationService {

    private final ModelMapper modelMapper = new ModelMapper();
    private static final Logger logger = LoggerFactory.getLogger(RemoteTokenAuthorizationServiceImpl.class);
    private static final String CHECK_TOKEN_FAILED = "remote.token.check.result.failed";
    public static final String REMOTE_TOKEN_AUTHORIZATION_DOES_NOT_EXIST_EXCEPTION = "error.remote.token.authorization.does.not.exist";
    private RemoteTokenAuthorizationMapper authorizationMapper;

    private RetrofitClientFactory retrofitClientFactory;

    public RemoteTokenAuthorizationServiceImpl(RemoteTokenAuthorizationMapper authorizationMapper, RetrofitClientFactory retrofitClientFactory) {
        this.authorizationMapper = authorizationMapper;
        this.retrofitClientFactory = retrofitClientFactory;
    }

    @Override
    public RemoteTokenAuthorizationVO storeAndCheckToken(String base64Token) {
        RemoteTokenAuthorizationVO tokenVO = RemoteTokenBase64Util.decode(base64Token);
        CommonCheckResultVO checkedResultVO = getCheckedResultVOWithOperationType(tokenVO, RemoteTokenCarryType.CONFIGURE_AND_TEST.value());
        saveTokenByCheckedResult(tokenVO, checkedResultVO);
        return authorizationMapper.selectLatestToken();
    }

    @Override
    public RemoteTokenAuthorizationVO checkLatestToken() {
        RemoteTokenAuthorizationVO authorizationVO = authorizationMapper.selectLatestToken();
        if (ObjectUtils.isEmpty(authorizationVO)) {
            return new RemoteTokenAuthorizationVO();
        }
        // 中断状态不检查更新
        if (!RemoteTokenStatus.BREAK.value().equals(authorizationVO.getStatus())) {
            CommonCheckResultVO resultVO = getCheckedResultVOWithOperationType(authorizationVO, null);
            updateTokenStatusByCheckedResult(authorizationVO, resultVO);
            authorizationVO = authorizationMapper.selectLatestToken();
        }
        loadAuthorizationVO(authorizationVO);
        return authorizationVO;
    }

    @Override
    public PageInfo<RemoteTokenAuthorizationVO> pagingAuthorizations(String name, String email, String status,
                                                                     String organizationName, String[] params, Pageable pageable) {
        PageInfo<RemoteTokenAuthorizationVO> authorizationVOPageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> authorizationMapper.fulltextSearch(name, email, status, organizationName, params));
        authorizationVOPageInfo.getList().forEach(this::loadAuthorizationVO);
        return authorizationVOPageInfo;
    }

    @Override
    public RemoteTokenAuthorizationVO breakLatestToken() {
        RemoteTokenAuthorizationVO authorizationVO = authorizationMapper.selectLatestToken();
        if (!RemoteTokenStatus.SUCCESS.value().equals(authorizationVO.getStatus())) {
            throw new CommonException("error.remote.token.status.not.success");
        }
        getCheckedResultVOWithOperationType(authorizationVO, RemoteTokenCarryType.DISCONNECT.value());
        changeTokenStatus(authorizationVO, RemoteTokenStatus.BREAK.value());
        return authorizationMapper.selectLatestToken();
    }

    @Override
    public RemoteTokenAuthorizationVO reconnectLatestToken() {
        RemoteTokenAuthorizationVO authorizationVO = authorizationMapper.selectLatestToken();
        if (!RemoteTokenStatus.BREAK.value().equals(authorizationVO.getStatus())) {
            throw new UpdateException("error.remote.token.status.not.break");
        }
        CommonCheckResultVO checkedResultVO = getCheckedResultVOWithOperationType(authorizationVO, RemoteTokenCarryType.RECONNECTION.value());
        updateTokenStatusByCheckedResult(authorizationVO, checkedResultVO);
        return authorizationMapper.selectLatestToken();
    }

    private void saveTokenByCheckedResult(RemoteTokenAuthorizationVO tokenVO, CommonCheckResultVO resultVO) {
        RemoteTokenAuthorizationDTO tokenDTO = new RemoteTokenAuthorizationDTO();
        modelMapper.map(tokenVO, tokenDTO);
        if (Boolean.TRUE.equals(resultVO.getFailed())) {
            tokenDTO.setStatus(RemoteTokenStatus.FAILED.value());
            if (resultVO.getFailMessage().equals(RemoteTokenCheckFailedType.NOTEXIST.value())) {
                throw new CommonException("error.remote.token.authorization.not.existed");
            } else if (resultVO.getFailMessage().equals(RemoteTokenCheckFailedType.EXPIRED.value())) {
                throw new CommonException("error.remote.token.authorization.expired");
            }
        }
        if (Boolean.FALSE.equals(resultVO.getFailed())) {
            tokenDTO.setStatus(RemoteTokenStatus.SUCCESS.value());
        }
        if (authorizationMapper.insertSelective(tokenDTO) != 1) {
            throw new CommonException("error.remote.token.authorization.insert");
        }
    }

    private void updateTokenStatusByCheckedResult(RemoteTokenAuthorizationVO authorizationVO, CommonCheckResultVO resultVO) {
        Assert.notNull(resultVO, "error.remote.token.check.result.empty");
        if (Boolean.TRUE.equals(resultVO.getFailed())) {
            changeTokenStatus(authorizationVO, RemoteTokenStatus.FAILED.value());
            if (resultVO.getFailMessage().equals(RemoteTokenCheckFailedType.NOTEXIST.value())) {
                throw new CommonException("error.remote.token.authorization.not.existed");
            }
            if (resultVO.getFailMessage().equals(RemoteTokenCheckFailedType.EXPIRED.value())) {
                throw new CommonException("error.remote.token.authorization.expired");
            }
        }
        if (Boolean.FALSE.equals(resultVO.getFailed())) {
            changeTokenStatus(authorizationVO, RemoteTokenStatus.SUCCESS.value());
        }
    }

    private CommonCheckResultVO getCheckedResultVOWithOperationType(RemoteTokenAuthorizationVO tokenVO, String carryType) {
        RemoteTokenRetrofitClient remoteTokenRetrofitClient = (RemoteTokenRetrofitClient)
                retrofitClientFactory.getRetrofitBean(tokenVO.getAuthorizationUrl(), RemoteTokenRetrofitClient.class);
        Call<ResponseBody> call = remoteTokenRetrofitClient.checkToken(tokenVO.getRemoteToken(), carryType);
        CommonCheckResultVO resultVO;
        try {
            resultVO = RetrofitCallExceptionParse.executeCall(call, "error.remote.token.check", CommonCheckResultVO.class);
        } catch (Exception e) {
            logger.info("::Retrofit::check resultVo exception");
            resultVO = new CommonCheckResultVO();
            resultVO.setFailed(true);
            resultVO.setFailMessage(CHECK_TOKEN_FAILED);
        }
        return resultVO;
    }

    private void changeTokenStatus(RemoteTokenAuthorizationVO v, String status) {
        RemoteTokenAuthorizationDTO oldToken = new RemoteTokenAuthorizationDTO();
        modelMapper.map(v, oldToken);
        oldToken.setObjectVersionNumber(v.getObjectVersionNumber());
        oldToken.setStatus(status);
        if (authorizationMapper.updateByPrimaryKey(oldToken) != 1) {
            throw new CommonException("error.remote.token.authorization.update");
        }
    }

    private void loadAuthorizationVO(RemoteTokenAuthorizationVO authorizationVO) {
        OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setName(authorizationVO.getOrganizationName());
        organizationDTO.setCode(authorizationVO.getOrganizationCode());
        authorizationVO.setEncryptionToken(RemoteTokenBase64Util.encode(authorizationVO.getName(),
                authorizationVO.getEmail(), authorizationVO.getRemoteToken(), authorizationVO.getAuthorizationUrl(), organizationDTO));
    }

}
