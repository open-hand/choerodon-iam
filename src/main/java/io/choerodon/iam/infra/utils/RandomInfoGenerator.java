package io.choerodon.iam.infra.utils;

import java.util.UUID;

import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.stereotype.Component;


/**
 * @author Eugen
 * @since 2019/02/20
 */
@Component
public class RandomInfoGenerator {

    private UserMapper userMapper;

//    private OrganizationMapper organizationMapper;

    private ProjectMapper projectMapper;

    // TODO 等待hzero 添加Tenant的字段，再实现这些方法
    public RandomInfoGenerator(UserMapper userMapper,
//                               OrganizationMapper organizationMapper,
                               ProjectMapper projectMapper) {
        this.userMapper = userMapper;
//        this.organizationMapper = organizationMapper;
        this.projectMapper = projectMapper;
    }

    /**
     * 生成符合以下条件的随机登录名.
     * 1.数据库中未存在
     * 2.10位小写字母或数字
     *
     * @return 符合条件的登录名
     */
    public String randomLoginName() {
        String loginName = "";
        boolean flag = false;
        while (!flag) {
            loginName = RandomStringUtils.randomAlphanumeric(10).toLowerCase();
            User user = new User();
            user.setLoginName(loginName);
            User userByLoginName = userMapper.selectOne(user);
            if (userByLoginName == null) {
                flag = true;
            }
        }
        return loginName;
    }

    /**
     * 生成随机项目编码
     * 1.数据库中未存在
     * 2.格式："proj-"+8位随机小写字母或数字
     *
     * @return 符合条件的项目编码
     */
    public String randomProjectCode(Long organizationId) {
        String projectCode = "";
        boolean flag = false;
        while (!flag) {
            projectCode = "proj-" + RandomStringUtils.randomAlphanumeric(8).toLowerCase();
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setCode(projectCode);
            projectDTO.setOrganizationId(organizationId);
            ProjectDTO projByCode = projectMapper.selectOne(projectDTO);
            if (projByCode == null) {
                flag = true;
            }
        }
        return projectCode;
    }

    public String generateUserToken() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }

}
