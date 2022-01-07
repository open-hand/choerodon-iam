package io.choerodon.iam.app.service;

import java.util.Map;
import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.TenantConfig;
import org.hzero.iam.domain.service.user.impl.DefaultUserDetailsService;
import org.hzero.iam.infra.mapper.TenantConfigMapper;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.constant.RedisCacheKeyConstants;
import io.choerodon.iam.infra.enums.TenantConfigEnum;
import io.choerodon.iam.infra.mapper.TenantConfigC7nMapper;
import io.choerodon.iam.infra.utils.TypeUtil;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/5/31
 * @Modified By:
 */
@Service
public class DefaultUserDetailsC7nService extends DefaultUserDetailsService {

    private final ThreadPoolExecutor SELECT_MENU_POOL = new ThreadPoolExecutor(20, 200, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000), new ThreadFactoryBuilder().setNameFormat("record-tenant-visitors-%d").build());

    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;
    @Autowired
    private TenantConfigC7nMapper tenantConfigC7nMapper;
//    private final Map<Long, Object> tenantObjects = new ConcurrentHashMap<>();

    @Override
    public void storeUserTenant(Long tenantId) {
        if (tenantId != null) {
            Tenant tenant = tenantMapper.selectByPrimaryKey(tenantId);
            if (tenant.getEnabledFlag() != null && tenant.getEnabledFlag() != 1) {
                throw new CommonException("error.tenant.enable");
            }
            CompletableFuture.runAsync(() -> updateVisitors(tenantId), SELECT_MENU_POOL);
            super.storeUserTenant(tenantId);
        }
    }

    /**
     * 记录访问人数
     *
     * @param tenantId
     */
    public void updateVisitors(Long tenantId) {
//        tenantObjects.computeIfAbsent(tenantId, k -> new Object());
//        synchronized (tenantObjects.get(tenantId)) {
//            String key = String.format(RedisCacheKeyConstants.TENANT_VISITORS_FORMAT, tenantId);
//            String visitorsStr = stringRedisTemplate.opsForValue().get(key);
//            Integer visitors = 0;
//            if (!StringUtils.isEmpty(visitorsStr)) {
//                visitors = TypeUtil.objToInteger(visitorsStr);
//            }
//            visitors = visitors + 1;
//            // 缓存超过100人存数据库
//            if (visitors >= 100) {
//                updateVisitorsForConfig(tenantId, visitors);
//                visitors = 0;
//            }
//            stringRedisTemplate.opsForValue().set(key, visitors.toString());
//        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateVisitorsForConfig(Long tenantId, Integer visitors) {
        TenantConfig config = tenantConfigC7nMapper.queryTenantConfigByTenantIdAndKey(tenantId, TenantConfigEnum.VISITORS.value());
        if (config == null) {
            config = new TenantConfig();
            config.setConfigKey(TenantConfigEnum.VISITORS.value());
            config.setConfigValue(visitors.toString());
            config.setTenantId(tenantId);
            if (tenantConfigMapper.insert(config) != 1) {
                throw new CommonException("error.insert.tenant.config");
            }
        } else {
            config.setConfigValue(visitors.toString());
            if (tenantConfigMapper.updateByPrimaryKeySelective(config) != 1) {
                throw new CommonException("error.update.tenant.config");
            }
        }
    }
}
