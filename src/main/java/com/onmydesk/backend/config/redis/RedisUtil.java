package com.onmydesk.backend.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public void setValues(String key, String data) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    @Transactional(readOnly = true)
    public String getValues(String key) {
        ValueOperations<String, Object> values = redisTemplate.opsForValue();
        if (values.get(key) == null) {
            return "false";
        }
        return (String) values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void expireValues(String key, int timeout, TimeUnit unit) {
        redisTemplate.expire(key, timeout, unit);
    }

    public void addToSet(String key, String value) {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        setOps.add(key, value);
    }

    public boolean isMemberOfSet(String key, String value) {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        return Boolean.TRUE.equals(setOps.isMember(key, value));
    }

    public Set<Object> getSetMembers(String key) {
        SetOperations<String, Object> setOps = redisTemplate.opsForSet();
        return setOps.members(key);
    }

    public Set<String> getKeysByPattern(String pattern) {
        return redisTemplate.keys(pattern);
    }

    public void setHashOps(String key, Map<String, String> data) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    @Transactional(readOnly = true)
    public String getHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        return Boolean.TRUE.equals(values.hasKey(key, hashKey)) ? (String) redisTemplate.opsForHash().get(key, hashKey) : "";
    }

    public void deleteHashOps(String key, String hashKey) {
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

    public boolean checkExistsValue(String value) {
        return !value.equals("false");
    }
}
