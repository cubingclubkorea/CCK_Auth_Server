package com.cubingclub.auth.Cubingclub_SSO_Server.Account.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthNumberService {
    private final StringRedisTemplate redisTemplate;

    private static final String AUTH_PREFIX = "auth:";
    private static final long AUTH_EXPIRE_MINUTES = 5;

    public String generateAndSave(String email) {
        String authNum = generateRandomNumber();
        redisTemplate.opsForValue().set(
                AUTH_PREFIX + email,
                authNum,
                AUTH_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );
        return authNum;
    }

    public Boolean verify(String email, String authNum) {
        String key = AUTH_PREFIX + email;
        String storedNum = redisTemplate.opsForValue().get(key);

        if (storedNum != null && storedNum.equals(authNum)) {
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    private String generateRandomNumber() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}