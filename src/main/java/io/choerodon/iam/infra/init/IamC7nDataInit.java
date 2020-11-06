package io.choerodon.iam.infra.init;

import org.hzero.core.message.MessageAccessor;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * @author scp
 * @date 2020/11/6
 * @description
 */
@Component
public class IamC7nDataInit implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        MessageAccessor.addBasenames("classpath:messages/messages_ciam");
    }
}
