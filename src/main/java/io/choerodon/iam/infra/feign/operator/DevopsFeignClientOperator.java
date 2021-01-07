package io.choerodon.iam.infra.feign.operator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.iam.api.vo.BarLabelRotationItemVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.infra.feign.DevopsFeignClient;

@Component
public class DevopsFeignClientOperator {
    @Autowired
    private DevopsFeignClient devopsFeignClient;

    public Boolean checkGitlabEmail(String email) {
        return FeignClientUtils.doRequest(() -> devopsFeignClient.checkGitlabEmail(email), Boolean.class, "error.feign.devops.check.gitlab.email");
    }

    public Map<Long, Integer> countAppServerByProjectId(Long projectId, List<Long> longList) {
        return FeignClientUtils.doRequest(() -> devopsFeignClient.countAppServerByProjectId(projectId, longList), new TypeReference<Map<Long, Integer>>() {
        }, "error.feign.devops.query.app.server");
    }

    public BarLabelRotationItemVO countByDate(Long projectId, String startTime, String endTime) {
        return FeignClientUtils.doRequest(() -> devopsFeignClient.countByDate(projectId, startTime, endTime), BarLabelRotationItemVO.class, "error.feign.devops.query.deploy.records");
    }

    public List<UserAttrVO> listByUserIds(Set<Long> iamUserIds) {
        return FeignClientUtils.doRequest(() -> devopsFeignClient.listByUserIds(iamUserIds), new TypeReference<List<UserAttrVO>>() {
        }, "error.feign.devops.query.gitlab.user.id");
    }

    public UserAttrVO queryByUserId(Long projectId, Long userId) {
        return FeignClientUtils.doRequest(() -> devopsFeignClient.queryByUserId(projectId, userId), UserAttrVO.class, "error.feign.devops.query.gitlab.user.id");
    }

}
