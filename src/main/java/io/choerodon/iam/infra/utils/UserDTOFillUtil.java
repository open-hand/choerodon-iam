package io.choerodon.iam.infra.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hzero.iam.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.choerodon.asgard.common.ApplicationContextHelper;
import io.choerodon.iam.app.service.UserC7nService;

/**
 * 〈功能简述〉
 * 〈填充用户DTO工具类〉
 *
 * @author wanghao
 * @Date 2021/7/3 14:37
 */
public class UserDTOFillUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDTOFillUtil.class);

    private UserDTOFillUtil() {
    }

    /**
     * 填充User
     * @param sourceList 源集合
     * @param filed userId字段
     * @param destFiled 目标DTO字段
     */
    public static void fillUserInfo(List<?> sourceList, String filed, String destFiled) {
        List<Long> userIds = sourceList.stream().map(v -> {
            Class<?> aClass = v.getClass();
            try {
                Field declaredField = aClass.getDeclaredField(filed);
                declaredField.setAccessible(true);
                return (Long) declaredField.get(v);
            } catch (Exception e) {
                LOGGER.info("read user id failed", e.fillInStackTrace());
            }
            return null;
        }).collect(Collectors.toList());
        Long[] newIds = new Long[userIds.size()];
        List<User> users = ApplicationContextHelper.getBean(UserC7nService.class).listUsersByIds(userIds.toArray(newIds), false);
        if(users != null) {
            Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, Function.identity()));
            sourceList.forEach(source -> {
                Class<?> aClass = source.getClass();
                try {
                    Field userIdFiled = aClass.getDeclaredField(filed);
                    userIdFiled.setAccessible(true);
                    Long userId = (Long) userIdFiled.get(source);

                    User user = userMap.get(userId);
                    Field userFiled = aClass.getDeclaredField(destFiled);
                    userFiled.setAccessible(true);
                    userFiled.set(source, user);
                } catch (Exception e) {
                    LOGGER.info("fill user id failed", e.fillInStackTrace());
                }
            });
        }
    }
}
