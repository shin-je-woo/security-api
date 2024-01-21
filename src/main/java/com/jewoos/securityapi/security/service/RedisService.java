package com.jewoos.securityapi.security.service;

import com.jewoos.securityapi.error.ErrorCode;
import com.jewoos.securityapi.error.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void put(String key, Object value, long expirationTime) {
        redisTemplate.opsForValue().set(
                key,
                value,
                expirationTime,
                TimeUnit.MILLISECONDS);
    }

    public String get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key))
                .orElseThrow(() -> new GlobalException(ErrorCode.NO_DATA))
                .toString();
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
