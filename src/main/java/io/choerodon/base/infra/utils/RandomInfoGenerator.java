package io.choerodon.base.infra.utils;

import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Component;

import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.mapper.ApplicationMapper;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.base.infra.mapper.UserMapper;

/**
 * @author Eugen
 * @since 2019/02/20
 */
@Component
public class RandomInfoGenerator {

    private UserMapper userMapper;

    private OrganizationMapper organizationMapper;

    private ProjectMapper projectMapper;

    public RandomInfoGenerator(UserMapper userMapper,
                               OrganizationMapper organizationMapper,
                               ProjectMapper projectMapper) {
        this.userMapper = userMapper;
        this.organizationMapper = organizationMapper;
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
            UserDTO userDTO = new UserDTO();
            userDTO.setLoginName(loginName);
            UserDTO userByLoginName = userMapper.selectOne(userDTO);
            if (userByLoginName == null) {
                flag = true;
            }
        }
        return loginName;
    }


    /**
     * 生成随机组织编码
     * 1.数据库中未存在
     * 2.格式："org-"+10位随机小写字母或数字
     *
     * @return 符合条件的组织编码
     */
    public String randomOrgCode() {
        String orgCode = "";
        boolean flag = false;
        while (!flag) {
            orgCode = "org-" + RandomStringUtils.randomAlphanumeric(10).toLowerCase();
            OrganizationDTO organizationDO = new OrganizationDTO();
            organizationDO.setCode(orgCode);
            OrganizationDTO orgByCode = organizationMapper.selectOne(organizationDO);
            if (orgByCode == null) {
                flag = true;
            }
        }
        return orgCode;
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
