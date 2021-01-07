package io.choerodon.iam.infra.feign.operator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.infra.feign.TestManagerFeignClient;

@Component
public class TestManagerFeignClientOperator {
    @Autowired
    private TestManagerFeignClient testManagerFeignClient;

    public AgileProjectInfoVO updateProjectInfo(Long projectId, AgileProjectInfoVO agileProjectInfoVO) {
        return FeignClientUtils.doRequest(() -> testManagerFeignClient.updateProjectInfo(projectId, agileProjectInfoVO), AgileProjectInfoVO.class, "error.update.agile.project.info");
    }
}
