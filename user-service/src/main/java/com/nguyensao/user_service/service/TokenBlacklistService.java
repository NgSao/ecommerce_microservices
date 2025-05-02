package com.nguyensao.user_service.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistService {
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklist(String token) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "true", Duration.ofHours(1));
    }

    public boolean isBlacklisted(String token) {
        return Boolean.TRUE.toString().equals(redisTemplate.opsForValue().get(BLACKLIST_PREFIX + token));
    }
}
