package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.agile.MigrateWorkGroupDataVO;

/**
 * @author superlee
 * @since 2022-07-21
 */
public interface WorkGroupMigrateService {

    /**
     * 同步敏捷的工作组数据
     *
     * @param migrateWorkGroupDataVO
     */
    void migrate(MigrateWorkGroupDataVO migrateWorkGroupDataVO);
}
