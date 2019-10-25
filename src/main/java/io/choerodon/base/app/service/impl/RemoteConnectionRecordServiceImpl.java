package io.choerodon.base.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.base.api.vo.OrgRemoteTokenConnRecordVO;
import io.choerodon.base.api.vo.RemoteConnectionRecordVO;
import io.choerodon.base.app.service.RemoteConnectionRecordService;
import io.choerodon.base.infra.dto.RemoteConnectionRecordDTO;
import io.choerodon.base.infra.mapper.RemoteConnectionRecordMapper;
import io.choerodon.base.infra.mapper.RemoteTokenMapper;
import io.choerodon.base.infra.utils.IpUtils;
import io.choerodon.base.infra.utils.RemoteTokenBase64Util;
import io.choerodon.core.exception.CommonException;
import io.choerodon.web.util.PageableHelper;

/**
 * @author Eugen
 **/
@Service
public class RemoteConnectionRecordServiceImpl implements RemoteConnectionRecordService {

    @Value("${choerodon.gateway.url}")
    private String gateway;

    private static final String REMOTE_CONNECTION_RECORD_INSERT_EXCEPTION = "error.remote.connection.record.insert.fail";
    private RemoteConnectionRecordMapper remoteConnectionRecordMapper;
    private RemoteTokenMapper remoteTokenMapper;

    public RemoteConnectionRecordServiceImpl(RemoteConnectionRecordMapper remoteConnectionRecordMapper, RemoteTokenMapper remoteTokenMapper) {
        this.remoteConnectionRecordMapper = remoteConnectionRecordMapper;
        this.remoteTokenMapper = remoteTokenMapper;
    }

    @Override
    public RemoteConnectionRecordDTO successRecord(Long remoteTokenId, String operation) {
        RemoteConnectionRecordDTO record = new RemoteConnectionRecordDTO();
        record.setRemoteTokenId(remoteTokenId);
        record.setOperation(operation);
        record.setSourceIp(new IpUtils().getIPAddr());
        if (remoteConnectionRecordMapper.insertSelective(record) != 1) {
            throw new CommonException(REMOTE_CONNECTION_RECORD_INSERT_EXCEPTION);
        }
        return remoteConnectionRecordMapper.selectByPrimaryKey(record.getId());
    }

    @Override
    public PageInfo<RemoteConnectionRecordVO> pageSearch(RemoteConnectionRecordVO filterVO, Pageable pageable, String[] params) {
        //1.分页查询
        PageInfo<RemoteConnectionRecordVO> pageInfo = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(()
                -> remoteConnectionRecordMapper.searchByParams(filterVO, params));
        if (pageInfo == null) {
            return new PageInfo<>();
        }
        //2.base64加密
        if (!CollectionUtils.isEmpty(pageInfo.getList())) {
            pageInfo.getList().forEach(h -> h.setRemoteTokenInBase64(RemoteTokenBase64Util.encode(h.getName(), h.getEmail(), h.getRemoteToken(), gateway,
                    Optional.ofNullable(remoteTokenMapper.selectOrganization(h.getRemoteToken())).orElseThrow(() -> new CommonException("error.remote.token.organization.not.found")))));
        }
        return pageInfo;
    }

    @Override
    public PageInfo<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecords(Pageable pageable, OrgRemoteTokenConnRecordVO filter) {
        // 1. 联合查询查出满足条件的组织id与最新日期
        // 2. 对每个组织循环查询具体详细信息，保证分页结果对应
        List<OrgRemoteTokenConnRecordVO> result = new ArrayList<>();
        filter.setQueryOrgIDs(1L);
        PageInfo<OrgRemoteTokenConnRecordVO> orgHasRecords = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), null).doSelectPageInfo(
                () -> remoteConnectionRecordMapper.pageOrgRemoteTokenConnRecords(filter));
        orgHasRecords.getList().forEach(i -> {
            OrgRemoteTokenConnRecordVO filterNew = new OrgRemoteTokenConnRecordVO();
            filterNew.setOperation(filter.getOperation());
            filterNew.setOrganizationId(i.getOrganizationId());
            filterNew.setConnectTime(i.getConnectTime());
            List<OrgRemoteTokenConnRecordVO> orgRemoteTokenConnRecordVOS = remoteConnectionRecordMapper.pageOrgRemoteTokenConnRecords(filterNew);
            if (!orgRemoteTokenConnRecordVOS.isEmpty()) {
                result.add(orgRemoteTokenConnRecordVOS.get(0));
            }
        });
        orgHasRecords.setList(result);
        return orgHasRecords;
    }

    @Override
    public PageInfo<OrgRemoteTokenConnRecordVO> pageOrgRemoteTokenConnRecordsByOrgId(Pageable pageable, OrgRemoteTokenConnRecordVO filter) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(
                () -> remoteConnectionRecordMapper.pageOrgRemoteTokenConnRecordsByOrgId(filter));
    }
}