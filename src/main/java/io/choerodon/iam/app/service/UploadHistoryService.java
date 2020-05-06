package io.choerodon.iam.app.service;


import io.choerodon.iam.api.vo.PermissionDescriptionVO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.payload.EurekaEventPayload;
import org.hzero.iam.domain.entity.Role;

import java.util.Map;

/**
 * @author superlee
 */
public interface UploadHistoryService {
    UploadHistoryDTO latestHistory(Long userId, String type, Long sourceId, String sourceType);

    /**
     * @author superlee
     */
    interface ParsePermissionService {

        /**
         * 解析swagger的文档树
         *
         * @param payload 接受的消息
         */
        void parser(EurekaEventPayload payload);

        void processDescriptions(String service, Map<String, PermissionDescriptionVO> descriptions);

        Map<String, Role> queryInitRoleByCode();
    }
}
