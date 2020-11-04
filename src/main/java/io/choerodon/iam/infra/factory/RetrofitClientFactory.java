package io.choerodon.iam.infra.factory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import io.choerodon.iam.infra.config.RetrofitConfig;


@Component
public class RetrofitClientFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private RetrofitConfig retrofitConfig;

    public RetrofitClientFactory(RetrofitConfig retrofitConfig) {
        this.retrofitConfig = retrofitConfig;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Object getRetrofitBean(String baseUrl, Class clientName) {
        retrofitConfig.setBaseUrl(baseUrl);
        return applicationContext.getBean(clientName.getSimpleName());
    }

}