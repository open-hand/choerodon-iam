package io.choerodon.iam.infra.feign.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.infra.feign.AgileFeignClient;

@Component
public class AgileFeignClientOperator {
    @Autowired
    private AgileFeignClient agileFeignClient;

    public AgileProjectInfoVO queryProjectInfoByProjectId(Long projectId) {
        return FeignClientUtils.doRequest(() -> agileFeignClient.queryProjectInfoByProjectId(projectId), AgileProjectInfoVO.class);
    }
}
