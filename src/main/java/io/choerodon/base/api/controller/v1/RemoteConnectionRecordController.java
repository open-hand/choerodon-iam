package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.OrgRemoteTokenConnRecordVO;
import io.choerodon.base.app.service.RemoteConnectionRecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.base.infra.enums.RemoteTokenCarryType;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Eugen
 **/
@RestController
@RequestMapping(value = "/v1/remote_connect_records")
public class RemoteConnectionRecordController {

    private RemoteConnectionRecordService remoteConnectionRecordService;

    public RemoteConnectionRecordController(RemoteConnectionRecordService remoteConnectionRecordService) {
        this.remoteConnectionRecordService = remoteConnectionRecordService;
    }

    @GetMapping(value = "/organization_config")
    @Permission(permissionWithin = true)
    @CustomPageRequest
    @ApiOperation(value = "查询配置过remoteToken的组织、remoteToken以及连接记录信息")
    public ResponseEntity<PageInfo<OrgRemoteTokenConnRecordVO>> pageOrgRemoteTokenConnRecord(@SortDefault(value = "id",direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                             @ApiIgnore @RequestParam(value = "operation", required = false) String operation,
                                                                                             @RequestParam(value = "remote_token_enable", required = false) Boolean remoteTokenEnable,
                                                                                             @RequestParam(value = "expired", required = false) Boolean expired,
                                                                                             @RequestParam(value = "organization_name", required = false) String organizationName,
                                                                                             @RequestParam(value = "param", required = false) String[] params) {
        OrgRemoteTokenConnRecordVO filterVO = new OrgRemoteTokenConnRecordVO();
        if (StringUtils.isEmpty(operation)) {
            filterVO.setOperation(RemoteTokenCarryType.CONFIGURE_AND_TEST.value());
        } else {
            filterVO.setOperation(operation);
        }
        if (!StringUtils.isEmpty(remoteTokenEnable)) {
            filterVO.setOrganizationRemoteTokenEnabled(remoteTokenEnable);
        }
        if (!StringUtils.isEmpty(organizationName)) {
            filterVO.setOrganizationName(organizationName);
        }
        if (!StringUtils.isEmpty(expired)) {
            filterVO.setExpired(expired);
        }
        if (!StringUtils.isEmpty(params)) {
            filterVO.setParams(params);
        }
        return new ResponseEntity<>(remoteConnectionRecordService.pageOrgRemoteTokenConnRecords(Pageable, filterVO), HttpStatus.OK);
    }

    @GetMapping
    @Permission(permissionWithin = true)
    @CustomPageRequest
    @ApiOperation(value = "查询指定组织的remoteToken连接记录信息")
    public ResponseEntity<PageInfo<OrgRemoteTokenConnRecordVO>> pageOrgRemoteTokenConnRecordsByOrgId(@SortDefault(value = "id",direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                                     @RequestParam(value = "organization_id") Long orgId,
                                                                                                     @RequestParam(value = "operation", required = false) String operation,
                                                                                                     @RequestParam(value = "name", required = false) String name,
                                                                                                     @RequestParam(value = "email", required = false) String email,
                                                                                                     @RequestParam(value = "source_ip", required = false) String sourceIp,
                                                                                                     @RequestParam(value = "param", required = false) String[] params) {
        OrgRemoteTokenConnRecordVO filterVO = new OrgRemoteTokenConnRecordVO();
        filterVO.setOrganizationId(orgId);
        if (!StringUtils.isEmpty(operation)) {
            filterVO.setOperation(operation);
        }
        if (!StringUtils.isEmpty(name)) {
            filterVO.setName(name);
        }
        if (!StringUtils.isEmpty(email)) {
            filterVO.setEmail(email);
        }
        if (!StringUtils.isEmpty(sourceIp)) {
            filterVO.setSourceIp(sourceIp);
        }
        if (!StringUtils.isEmpty(params)) {
            filterVO.setParams(params);
        }
        return new ResponseEntity<>(remoteConnectionRecordService.pageOrgRemoteTokenConnRecordsByOrgId(Pageable, filterVO), HttpStatus.OK);
    }

}
