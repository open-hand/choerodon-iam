package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.AppVerDownloadRecordService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.AppVerDownloadRecordDTO;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@RestController
@RequestMapping(value = "/v1/applications/download_records")
public class AppVerDownloadRecordController extends BaseController {

    private AppVerDownloadRecordService appVerDownloadRecordService;

    public AppVerDownloadRecordController(AppVerDownloadRecordService appVerDownloadRecordService) {
        this.appVerDownloadRecordService = appVerDownloadRecordService;
    }

    @ApiOperation(value = "查询所有应用的历史下载记录")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<AppVerDownloadRecordDTO>> pagingAppDownloadRecord(@ApiIgnore
                                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                     @RequestParam(required = false) String appName,
                                                                                     @RequestParam(required = false) String categoryName,
                                                                                     @RequestParam(required = false) String downloader,
                                                                                     @RequestParam(required = false) String versionName,
                                                                                     @RequestParam(required = false) String status,
                                                                                     @RequestParam("organization_id") Long organizationId,
                                                                                     @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(appVerDownloadRecordService.pagingAppDownloadRecord(Pageable, appName, categoryName, organizationId, downloader, versionName, status, params), HttpStatus.OK);
    }

}
