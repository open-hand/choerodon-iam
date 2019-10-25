package io.choerodon.base.infra.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by superlee on 2018/10/10.
 */
@Component
public class RedisHelper {

    public static final String NAME_SPACE = "choerodon:org";

    private StringRedisTemplate redisTemplate;

    public RedisHelper(StringRedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    public boolean hasKey(String key) {
        key = NAME_SPACE + ":" + key;
        return redisTemplate.hasKey(key);
    }


    public void set(String key, String value, Long timeout) {
        key = NAME_SPACE + ":" + key;
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void set(String key, String value, Long timeout, TimeUnit unit) {
        key = NAME_SPACE + ":" + key;
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public String get(String key) {
        key = NAME_SPACE + ":" + key;
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key) {
        key = NAME_SPACE + ":" + key;
        redisTemplate.delete(key);
    }
}
