package io.choerodon.iam.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hzero.iam.domain.entity.User;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.UserNumberVO;
import io.choerodon.iam.api.vo.UserWithGitlabIdVO;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
public interface UserC7nService {

    User queryInfo(Long userId);

    User updateInfo(User user, Boolean checkLogin);

    CustomUserDetails checkLoginUser(Long id);

    String uploadPhoto(Long id, MultipartFile file);

    String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

    void check(User user);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id数组
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<UserDTO> 用户集合
     */
    List<User> listUsersByIds(Long[] ids, Boolean onlyEnabled);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<UserDTO> 用户集合
     */
    List<UserWithGitlabIdVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled);

    /**
     * 根据用户emails集合查询用户的集合
     *
     * @param emails 用户email数组
     * @return List<UserDTO> 用户集合
     */
    List<User> listUsersByEmails(String[] emails);


    /**
     * 根据loginName集合查询所有用户
     */
    List<User> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled);

    Long queryOrgIdByEmail(String email);

    Map<String, Object> queryAllAndNewUsers();


    /**
     * 按时间段统计组织或平台人数
     *
     * @param organizationId 如果为null，则统计平台人数
     * @param startTime
     * @param endTime
     * @return
     */
    UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime);



}
