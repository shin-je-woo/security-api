package com.jewoos.securityapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/token")
    public String refreshToken() {
        HashOperations<String, Object, Object> ops = redisTemplate.opsForHash();
        ops.put("refreshToken", "22222", "userId2");

        return "success;";
    }
}
