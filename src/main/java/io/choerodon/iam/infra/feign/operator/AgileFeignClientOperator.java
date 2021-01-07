package io.choerodon.iam.infra.feign.operator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.core.util.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.infra.feign.AgileFeignClient;

@Component
public class AgileFeignClientOperator {
    @Autowired
    private AgileFeignClient agileFeignClient;

    public AgileProjectInfoVO updateProjectInfo(Long projectId, AgileProjectInfoVO agileProjectInfoVO) {
        return FeignClientUtils.doRequest(() -> agileFeignClient.updateProjectInfo(projectId, agileProjectInfoVO), AgileProjectInfoVO.class);
    }

    public AgileProjectInfoVO queryProjectInfoByProjectId(Long projectId) {
        return FeignClientUtils.doRequest(() -> agileFeignClient.queryProjectInfoByProjectId(projectId), AgileProjectInfoVO.class);
    }
}
