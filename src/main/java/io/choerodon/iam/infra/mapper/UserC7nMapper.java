package io.choerodon.iam.infra.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.User;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
public interface UserC7nMapper extends BaseMapper<User> {
    List<User> listUsersByIds(@Param("ids") Long[] ids, @Param("onlyEnabled") Boolean onlyEnabled);

    List<User> listUsersByEmails(@Param("emails") String[] emails);

    /**
     * 根据用户登录名集合查所有用户
     *
     * @param loginNames
     * @param onlyEnabled
     * @return
     */
    List<User> listUsersByLoginNames(@Param("loginNames") String[] loginNames,
                                     @Param("onlyEnabled") Boolean onlyEnabled);

    /**
     * 全平台新增用户数（包括停用）
     *
     * @return 返回时间段内新增用户数
     */
    Integer newUsersByDate(@Param("begin") String begin,
                           @Param("end") String end);

    /**
     * 统计指定时间前平台或组织人数
     *
     * @param organizationId 如果为null，则统计平台人数
     * @param startTime
     * @return
     */
    long countPreviousNumberByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                           @Param("startTime") Date startTime);

    /**
     * 查询指定时间平台或组织新增人数
     *
     * @param organizationId 如果为null，则查询平台人数
     * @param startTime
     * @param endTime
     * @return
     */
    List<User> selectByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                       @Param("startTime") Date startTime,
                                       @Param("endTime") Date endTime);
}

