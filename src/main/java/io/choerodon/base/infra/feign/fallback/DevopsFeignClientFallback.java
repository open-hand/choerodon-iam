package io.choerodon.base.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionVO;
import io.choerodon.base.api.vo.BarLabelRotationItemVO;
import io.choerodon.base.infra.dto.devops.*;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eugen
 */
public class DevopsFeignClientFallback implements DevopsFeignClient {
    @Override
    public ResponseEntity<Boolean> checkGitlabEmail(String email) {
        throw new CommonException("error.feign.devops.check.gitlab.email");
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceUploadPayload>> pageByAppId(Long appId, int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<List<AppServiceVersionUploadPayload>> listVersionsByAppServiceId(Long appServiceId) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceDetailsVO>> batchQueryAppService(Long projectId, Set<Long> ids, Boolean doPage, int page, int size, List<String> sort, String params) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceDetailsVO>> batchQueryAppServiceWithOrg(Long organizationsId, Set<Long> ids, Boolean doPage, int page, int size, List<String> sort, String params) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceVO>> listAppByProjectId(Long projectId, Boolean doPage, int page, int size, List<String> sort, String params) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<AppServiceRepVO>> pageShareApps(Long projectId, Boolean doPage, int page, int size, List<String> sort, String searchParam) {
        return null;
    }


    @Override
    public ResponseEntity<List<AppServiceAndVersionDTO>> getSvcVersionByVersionIds(List<AppServiceAndVersionDTO> andVersionDTOS) {
        return null;
    }

    @Override
    public ResponseEntity<List<AppServiceVO>> listServiceByVersionIds(Long projectId, Set<Long> ids) {
        return null;
    }

    @Override
    public ResponseEntity<List<AppServiceVersionVO>> listVersionById(Long projectId, String id, String params) {
        return null;
    }

    @Override
    public ResponseEntity<Map<Long, Integer>> countAppServerByProjectId(Long aLong, List<Long> longs) {
        throw new CommonException("error.feign.devops.query.app.server");
    }

    @Override
    public ResponseEntity<BarLabelRotationItemVO> countByDate(Long projectId, Date startTime, Date endTime) {
        throw new CommonException("error.feign.devops.query.deploy.records");
    }

    @Override
    public ResponseEntity<List<UserAttrVO>> listByUserIds(Long projectId, Set<Long> iamUserIds) {
        throw new CommonException("error.feign.devops.query.gitlab.user.id");
    }
}
