package io.choerodon.iam.infra.interceptor;

import static io.choerodon.iam.infra.utils.SagaTopic.User.USER_UPDATE;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.infra.dto.payload.UserEventPayload;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/6/28
 * @Modified By:
 */
@Component
public class UpdateUserEmailInterceptor implements HandlerInterceptor<User> {
    @Autowired
    private TransactionalProducer producer;

    @Override
    public void interceptor(User user) {
        user.isEmailChanged();
        if (user.isEmailChanged()) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setEmail(user.getEmail());
            userEventPayload.setPhone(user.getPhone());
            userEventPayload.setId(user.getId().toString());
            userEventPayload.setName(user.getRealName());
            userEventPayload.setUsername(user.getLoginName());
            try {
                producer.apply(StartSagaBuilder.newBuilder()
                                .withSagaCode(USER_UPDATE)
                                .withRefType("user")
                                .withRefId(userEventPayload.getId())
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(user.getOrganizationId())
                                .withPayloadAndSerialize(userEventPayload),
                        builder -> {
                        });
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.updateUser.event", e);
            }
        }
    }
}