package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.utils.SagaTopic.RemoteToken.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.CommonCheckResultVO;
import io.choerodon.base.api.dto.RemoteTokenBase64VO;
import io.choerodon.base.api.dto.payload.OrganizationRemoteTokenPayload;
import io.choerodon.base.app.service.RemoteConnectionRecordService;
import io.choerodon.base.app.service.RemoteTokenService;
import io.choerodon.base.infra.dto.RemoteTokenDTO;
import io.choerodon.base.infra.enums.RemoteTokenCarryType;
import io.choerodon.base.infra.enums.RemoteTokenCheckFailedType;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.mapper.RemoteTokenMapper;
import io.choerodon.base.infra.utils.RemoteTokenBase64Util;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.web.util.PageableHelper;

/**
 * @author Eugen
 **/
@Service
public class RemoteTokenServiceImpl implements RemoteTokenService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${choerodon.gateway.url}")
    private String gateway;

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    private RemoteConnectionRecordService remoteConnectionRecordService;
    private RemoteTokenMapper remoteTokenMapper;
    private TransactionalProducer producer;
    private OrganizationMapper organizationMapper;


    public RemoteTokenServiceImpl(RemoteConnectionRecordService remoteConnectionRecordService,
                                  RemoteTokenMapper remoteTokenMapper,
                                  TransactionalProducer producer,
                                  OrganizationMapper organizationMapper) {
        this.remoteConnectionRecordService = remoteConnectionRecordService;
        this.remoteTokenMapper = remoteTokenMapper;
        this.producer = producer;
        this.organizationMapper = organizationMapper;
    }

    @Override
    public PageInfo<RemoteTokenBase64VO> pagingTheHistoryList(Long organizationId, Pageable pageable, RemoteTokenDTO filterDTO, String params) {
        //1.查询组织下最新令牌主键
        RemoteTokenBase64VO latestVO = remoteTokenMapper.selectLatestUnderOrg(organizationId);
        Long excludedId = (latestVO != null && latestVO.getLatestExpirationTime() == null) ? latestVO.getId() : null;
        //2.分页查询历史记录
        PageInfo<RemoteTokenBase64VO> historyPageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort()))
                .doSelectPageInfo(() -> remoteTokenMapper.filterHistoryUnderOrg(organizationId, excludedId, filterDTO, params));
        //3.base64加密
        if (!CollectionUtils.isEmpty(historyPageInfo.getList())) {
            historyPageInfo.getList().forEach(h -> h.setRemoteTokenInBase64(RemoteTokenBase64Util.encode(h.getName(), h.getEmail(), h.getRemoteToken(), gateway,
                    Optional.ofNullable(remoteTokenMapper.selectOrganization(h.getRemoteToken())).orElseThrow(() -> new CommonException("error.remote.token.organization.not.found")))));
        }
        return historyPageInfo;
    }

    @Override
    public RemoteTokenBase64VO getTheLatest(Long organizationId) {
        //1.查询组织下最新令牌主键
        RemoteTokenBase64VO latestVO = remoteTokenMapper.selectLatestUnderOrg(organizationId);
        //2.base64加密
        if (latestVO != null) {
            latestVO.setRemoteTokenInBase64(RemoteTokenBase64Util.encode(latestVO.getName(), latestVO.getEmail(), latestVO.getRemoteToken(), gateway,
                    Optional.ofNullable(remoteTokenMapper.selectOrganization(latestVO.getRemoteToken())).orElseThrow(() -> new CommonException("error.remote.token.organization.not.found"))));
        } else {
            latestVO = new RemoteTokenBase64VO();
        }
        return latestVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = REMOTE_TOKEN_ADD, description = "组织更新远程连接Token", inputSchemaClass = OrganizationRemoteTokenPayload.class)
    public RemoteTokenBase64VO createNewOne(Long organizationId, RemoteTokenDTO createDTO) {
        //1.将组织下所有令牌置为失效
        RemoteTokenDTO selectDTO = new RemoteTokenDTO();
        selectDTO.setExpired(false);
        List<RemoteTokenDTO> allUnexpired = remoteTokenMapper.select(selectDTO);
        if (allUnexpired != null) {
            allUnexpired.forEach(u -> {
                u.setExpired(true);
                u.setLatestExpirationTime(new Date());
                if (remoteTokenMapper.updateByPrimaryKeySelective(u) != 1) {
                    throw new CommonException("error.remote.token.expired");
                }
            });
        }
        //2.创建最新生效令牌
        createDTO.setRemoteToken(UUID.randomUUID().toString());
        createDTO.setExpired(false);
        if (remoteTokenMapper.insertSelective(createDTO) != 1) {
            throw new CommonException("error.remote.token.insert");
        }
        //3.saga事务
        if (devopsMessage) {
            sendEvent(REMOTE_TOKEN_ADD, new OrganizationRemoteTokenPayload()
                    .setOrganizationId(createDTO.getOrganizationId())
                    .setEmail(createDTO.getEmail())
                    .setName(createDTO.getName())
                    .setExpired(createDTO.getExpired())
                    .setRemoteToken(createDTO.getRemoteToken()));
        }
        //4.返回编码数据
        return getTheLatest(organizationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = REMOTE_TOKEN_EXPIRED, description = "组织失效远程连接Token", inputSchemaClass = OrganizationRemoteTokenPayload.class)
    public RemoteTokenBase64VO expired(Long organizationId, Long id) {
        //1.校验存在
        RemoteTokenDTO remoteTokenDTO = remoteTokenMapper.selectByPrimaryKey(id);
        if (remoteTokenDTO == null) {
            throw new CommonException("error.remote.token.not.exist");
        }
        //2.置为失效 且更新失效时间
        if (remoteTokenDTO.getExpired()) {
            throw new CommonException("error.remote.token.already.expired");
        }
        remoteTokenDTO.setExpired(true);
        remoteTokenDTO.setLatestExpirationTime(new Date());
        if (remoteTokenMapper.updateByPrimaryKeySelective(remoteTokenDTO) != 1) {
            throw new CommonException("error.remote.token.expired");
        }
        //3.saga事务
        if (devopsMessage) {
            sendEvent(REMOTE_TOKEN_EXPIRED, new OrganizationRemoteTokenPayload()
                    .setOrganizationId(remoteTokenDTO.getOrganizationId())
                    .setEmail(remoteTokenDTO.getEmail())
                    .setName(remoteTokenDTO.getName())
                    .setExpired(remoteTokenDTO.getExpired())
                    .setRemoteToken(remoteTokenDTO.getRemoteToken()));
        }
        //4.返回编码数据
        return getTheLatest(organizationId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = REMOTE_TOKEN_RENEWAL, description = "组织生效远程连接Token", inputSchemaClass = OrganizationRemoteTokenPayload.class)
    public RemoteTokenBase64VO renewal(Long organizationId, Long id) {
        //1.校验存在
        RemoteTokenDTO remoteTokenDTO = remoteTokenMapper.selectByPrimaryKey(id);
        if (remoteTokenDTO == null) {
            throw new CommonException("error.remote.token.not.exist");
        }
        //2.置为生效
        if (!remoteTokenDTO.getExpired()) {
            throw new CommonException("error.remote.token.does.not.expire");
        }
        remoteTokenDTO.setExpired(false);
        if (remoteTokenMapper.updateByPrimaryKeySelective(remoteTokenDTO) != 1) {
            throw new CommonException("error.remote.token.renewal");
        }
        //3.saga事务
        if (devopsMessage) {
            sendEvent(REMOTE_TOKEN_RENEWAL, new OrganizationRemoteTokenPayload()
                    .setOrganizationId(remoteTokenDTO.getOrganizationId())
                    .setEmail(remoteTokenDTO.getEmail())
                    .setName(remoteTokenDTO.getName())
                    .setExpired(remoteTokenDTO.getExpired())
                    .setRemoteToken(remoteTokenDTO.getRemoteToken()));
        }
        //4.返回编码数据
        return getTheLatest(organizationId);
    }


    @Override
    public CommonCheckResultVO checkToken(String remoteToken, String operation) {
        CommonCheckResultVO resultDTO = new CommonCheckResultVO();
        resultDTO.setFailed(false);
        // 构造校验dto
        RemoteTokenDTO checkDTO = new RemoteTokenDTO();
        checkDTO.setRemoteToken(remoteToken);
        RemoteTokenDTO remoteTokenDTO = remoteTokenMapper.selectOne(checkDTO);

        if (remoteTokenDTO != null) {
            // 根据操作类型执行相应操作
            if (RemoteTokenCarryType.DISCONNECT.value().equals(operation)) {
                // 执行断开连接操作 记录操作信息
                remoteConnectionRecordService.successRecord(remoteTokenDTO.getId(), RemoteTokenCarryType.DISCONNECT.value());
            } else {
                if (!organizationMapper.selectByPrimaryKey(remoteTokenDTO.getOrganizationId()).getRemoteTokenEnabled()) {
                    // 存在token但是组织层已关闭远程连接功能
                    resultDTO.setFailed(true);
                    resultDTO.setFailMessage(RemoteTokenCheckFailedType.FUNCTION_CLOSED.value());
                } else if (remoteTokenDTO.getExpired()) {
                    // 存在但已停用 则校验失败，失败原因为已停用
                    resultDTO.setFailed(true);
                    resultDTO.setFailMessage(RemoteTokenCheckFailedType.EXPIRED.value());
                } else {
                    //如校验成功，则记录
                    if (RemoteTokenCarryType.CONFIGURE_AND_TEST.value().equals(operation)) {
                        remoteConnectionRecordService.successRecord(remoteTokenDTO.getId(), RemoteTokenCarryType.CONFIGURE_AND_TEST.value());
                    }
                    if (RemoteTokenCarryType.RECONNECTION.value().equals(operation)) {
                        remoteConnectionRecordService.successRecord(remoteTokenDTO.getId(), RemoteTokenCarryType.RECONNECTION.value());
                    }
                }
            }
        } else {
            // 不存在 则校验失败，失败原因为不存在
            resultDTO.setFailed(true);
            resultDTO.setFailMessage(RemoteTokenCheckFailedType.NOTEXIST.value());
        }

        return resultDTO;
    }

    /**
     * 发送远程连接Token的事件
     *
     * @param sagaCode 事务编码（新建 或 失效）
     * @param payload  发送数据
     */
    private void sendEvent(String sagaCode, OrganizationRemoteTokenPayload payload) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("organization")
                        .withSagaCode(sagaCode),
                builder -> {
                    builder
                            .withPayloadAndSerialize(payload)
                            .withRefId(String.valueOf(payload.getOrganizationId()))
                            .withSourceId(payload.getOrganizationId());
                    return payload;
                });
    }
}
