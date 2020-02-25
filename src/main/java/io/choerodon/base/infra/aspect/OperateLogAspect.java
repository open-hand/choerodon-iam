package io.choerodon.base.infra.aspect;

import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.mapper.OperateLogMapper;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class OperateLogAspect {

    //解锁用户
    private static final String unlockUser = "unlockUser";
    //启用用户
    private static final String enableUser = "enableUser";
    //禁用用户
    private static final String disableUser = "disableUser";

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Pointcut("bean(*ServiceImpl) && @annotation(io.choerodon.base.infra.annotation.OperateLog)")
    public void updateMethodPointcut() {
        throw new UnsupportedOperationException();
    }


    @Around("updateMethodPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Long operatorId = DetailsHelper.getUserDetails().getUserId();
        OperateLogDTO operateLogDTO = new OperateLogDTO();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        OperateLog operateLog = method.getAnnotation(OperateLog.class);
        Object[] args = pjp.getArgs();
        String type = operateLog.type();
        String content = operateLog.content();
        ResourceType[] level = operateLog.level();
        if (null != operateLog && null != method && null != type) {
            switch (type) {
                case unlockUser:
                    content = handleUnlockUserOperateLog(content, operatorId, args);
                    break;
                case enableUser:
                    content = handleEnableUserOperateLog(content, operatorId, args);
                    break;
                default:
                    break;
            }
        }
        Object object = null;
        operateLogDTO.setOperatorId(operatorId);
        operateLogDTO.setContent(content);
        operateLogDTO.setSuccess(true);
        operateLogDTO.setMethod(method.getName());
        operateLogDTO.setType(type);
        try {
            object = pjp.proceed();
            Stream.of(level).forEach(v -> {
                operateLogDTO.setLevel(v.value());
                operateLogMapper.insert(operateLogDTO);
            });
        } catch (Throwable e) {
            operateLogDTO.setSuccess(false);
            Stream.of(level).forEach(v -> {
                operateLogDTO.setLevel(v.value());
                if (operateLogMapper.insert(operateLogDTO) != 1) {
                    throw new CommonException("error.operate.log.methodExecute", e);
                }
            });
        }
        return object;
    }

    private String handleEnableUserOperateLog(String content, Long operatorId, Object[] args) {
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        UserDTO targeter = userMapper.selectByPrimaryKey(args[1]);
        String parms = targeter.getId() + "(" + targeter.getRealName() + ")";
        if (!Objects.isNull(operator) && !Objects.isNull(targeter)) {
            return String.format(content, parms, operator.getRealName());
        }
        return null;
    }

    private String handleUnlockUserOperateLog(String content, Long operatorId, Object[] args) {
        UserDTO operator = userMapper.selectByPrimaryKey(operatorId);
        UserDTO targeter = userMapper.selectByPrimaryKey(args[1]);
        String parms = targeter.getId() + "(" + targeter.getRealName() + ")";
        if (!Objects.isNull(operator) && !Objects.isNull(targeter)) {
            return String.format(content, operator.getRealName(), parms);
        }
        throw new CommonException("error.query.user");
    }
}
