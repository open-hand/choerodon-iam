package io.choerodon.base.app.service;


import io.choerodon.annotation.entity.PermissionDescription;
import io.choerodon.core.swagger.PermissionData;
import io.choerodon.eureka.event.EurekaEventPayload;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.UploadHistoryDTO;

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

        void processDescriptions(String service, Map<String, PermissionDescription> descriptions);

        Map<String, RoleDTO> queryInitRoleByCode();
    }
}
