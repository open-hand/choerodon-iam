package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.User.USER_UPDATE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.common.HZeroService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.UserNumberVO;
import io.choerodon.iam.api.vo.UserWithGitlabIdVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.feign.FileFeignClient;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.payload.UserEventPayload;
import io.choerodon.iam.infra.utils.ImageUtils;
import io.choerodon.iam.app.service.UserC7nService;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class UserC7nServiceImpl implements UserC7nService {
    private static final String USER_NOT_LOGIN_EXCEPTION = "error.user.not.login";
    private static final String USER_NOT_FOUND_EXCEPTION = "error.user.not.found";
    private static final String USER_ID_NOT_EQUAL_EXCEPTION = "error.user.id.not.equals";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserC7nServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionalProducer producer;
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private FileFeignClient fileFeignClient;

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    @Autowired
    private OrganizationAssertHelper organizationAssertHelper;
    @Autowired
    private UserAssertHelper userAssertHelper;
    @Autowired
    private DevopsFeignClient devopsFeignClient;


    @Override
    public User queryInfo(Long userId) {
        User user = userAssertHelper.userNotExisted(userId);
        Tenant tenant = organizationAssertHelper.notExisted(user.getOrganizationId());
        user.setTenantName(tenant.getTenantName());
        user.setTenantNum(tenant.getTenantName());
        return user;
    }

    @Override
    public User updateInfo(User user, Boolean checkLogin) {
        if (checkLogin) {
            checkLoginUser(user.getId());
        }
        User dto;
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            dto = userService.updateUser(user);
            userEventPayload.setEmail(dto.getEmail());
            userEventPayload.setId(dto.getId().toString());
            userEventPayload.setName(dto.getRealName());
            userEventPayload.setUsername(dto.getLoginName());
            BeanUtils.copyProperties(dto, dto);
            try {
                producer.apply(StartSagaBuilder.newBuilder()
                                .withSagaCode(USER_UPDATE)
                                .withPayloadAndSerialize(userEventPayload)
                                .withRefId(dto.getId() + "")
                                .withRefType("user"),
                        builder -> {
                        }
                );
            } catch (Exception e) {
                throw new CommonException("error.UserService.updateInfo.event", e);
            }
        } else {
            dto = userService.updateUser(user);
        }
        Tenant organizationDTO = organizationAssertHelper.notExisted(dto.getOrganizationId());
        dto.setTenantName(organizationDTO.getTenantName());
        dto.setTenantNum(organizationDTO.getTenantNum());
        return dto;
    }

    @Override
    public String uploadPhoto(Long id, MultipartFile file) {
        checkLoginUser(id);
        return fileFeignClient.uploadFile(HZeroService.Iam.NAME, null, file.getOriginalFilename(), 0, null, file).getBody();
    }

    @Override
    public String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        checkLoginUser(id);
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return fileFeignClient.uploadFile(HZeroService.Iam.NAME, null, file.getOriginalFilename(), 0, null, file).getBody();
        } catch (Exception e) {
            LOGGER.warn("error happened when save photo {}", e.getMessage());
            throw new CommonException("error.user.photo.save");
        }
    }

    @Override
    public void check(User user) {
        boolean checkEmail = !StringUtils.isEmpty(user.getEmail());
        boolean checkPhone = !StringUtils.isEmpty(user.getPhone());

        if (!checkEmail && !checkPhone) {
            throw new CommonException("error.user.validation.fields.empty");
        }
        if (checkEmail) {
            checkEmail(user);
        }
        if (checkPhone) {
            checkPhone(user);
        }
    }


    @Override
    public CustomUserDetails checkLoginUser(Long id) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            throw new CommonException(USER_NOT_LOGIN_EXCEPTION);
        }
        if (!id.equals(customUserDetails.getUserId())) {
            throw new CommonException(USER_ID_NOT_EQUAL_EXCEPTION);
        }
        return customUserDetails;
    }


    @Override
    public List<User> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(ids)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByIds(ids, onlyEnabled);
        }
    }

    @Override
    public List<UserWithGitlabIdVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        } else {
            List<User> users = userC7nMapper.listUsersByIds(ids.toArray(new Long[0]), onlyEnabled);
            List<UserAttrVO> userAttrVOS = devopsFeignClient.listByUserIds(ids).getBody();
            if (userAttrVOS == null) {
                userAttrVOS = new ArrayList<>();
            }
            Map<Long, Long> userIdMap = userAttrVOS.stream().collect(Collectors.toMap(UserAttrVO::getIamUserId, UserAttrVO::getGitlabUserId));
            // 填充gitlabUserId
            return users.stream().map(user -> toUserWithGitlabIdDTO(user, userIdMap.get(user.getId()))).collect(Collectors.toList());
        }
    }

    @Override
    public List<User> listUsersByEmails(String[] emails) {
        if (ObjectUtils.isEmpty(emails)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByEmails(emails);
        }
    }

    @Override
    public List<User> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(loginNames)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByLoginNames(loginNames, onlyEnabled);
        }
    }

    @Override
    public Long queryOrgIdByEmail(String email) {
        return userAssertHelper.userNotExisted(UserAssertHelper.WhichColumn.EMAIL, email).getOrganizationId();
    }

    @Override
    public Map<String, Object> queryAllAndNewUsers() {
        Map<String, Object> map = new HashMap<>();
        User dto = new User();
        map.put("allUsers", userMapper.selectCount(dto));
        LocalDate localDate = LocalDate.now();
        String begin = localDate.toString();
        String end = localDate.plusDays(1).toString();
        map.put("newUsers", userC7nMapper.newUsersByDate(begin, end));
        return map;
    }

    @Override
    public UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime) {
        UserNumberVO userNumberVO = new UserNumberVO();
        long previousNumber = userC7nMapper.countPreviousNumberByOrgIdAndDate(organizationId, new java.sql.Date(startTime.getTime()));
        List<User> userDTOS = userC7nMapper.selectByOrgIdAndDate(organizationId,
                new java.sql.Date(startTime.getTime()),
                new java.sql.Date(endTime.getTime()));
        // 按日期分组
        Map<String, List<User>> userMap = userDTOS.stream()
                .collect(Collectors.groupingBy(t -> new java.sql.Date(t.getCreationDate().getTime()).toString()));

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();
        long totalNumber = previousNumber;
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            long newUserNumber = 0;
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<User> userList = userMap.get(date);
            if (!CollectionUtils.isEmpty(userList)) {
                newUserNumber = userList.size();
                totalNumber += newUserNumber;
            }

            userNumberVO.getDateList().add(date);
            userNumberVO.getTotalUserNumberList().add(totalNumber);
            userNumberVO.getNewUserNumberList().add(newUserNumber);

            startDate = startDate.plusDays(1);
        }

        return userNumberVO;
    }

    /**
     * 校验在启用用户中手机号唯一
     *
     * @param user 用户信息
     */
    private void checkPhone(User user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String phone = user.getPhone();
        User userDTO = new User();
        userDTO.setPhone(phone);
        userDTO.setEnabled(true);
        if (createCheck) {
            List<User> select = userMapper.select(userDTO);
            boolean existed = select != null && select.size() != 0;
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        } else {
            Long id = user.getId();
            User dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        }
    }

    private void checkEmail(User user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String email = user.getEmail();
        User userDTO = new User();
        userDTO.setEmail(email);
        if (createCheck) {
            boolean existed = userMapper.selectOne(userDTO) != null;
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        } else {
            Long id = user.getId();
            User dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        }
    }

    private UserWithGitlabIdVO toUserWithGitlabIdDTO(User user, @Nullable Long gitlabUserId) {
        if (user == null) {
            return null;
        }
        UserWithGitlabIdVO userWithGitlabIdVO = new UserWithGitlabIdVO();
        BeanUtils.copyProperties(user, userWithGitlabIdVO);
        userWithGitlabIdVO.setGitlabUserId(gitlabUserId);
        return userWithGitlabIdVO;
    }
}
