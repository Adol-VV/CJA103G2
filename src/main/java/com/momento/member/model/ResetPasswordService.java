package com.momento.member.model;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService {

	@Autowired
	private StringRedisTemplate redisTemplate;

	private static final String RESET_PREFIX = "pwd_reset:";

	// 1. 產生 Token 並存入 Redis，設定 30 分鐘過期
	public String createResetToken(String userId) {
		String token = UUID.randomUUID().toString();
		String key = RESET_PREFIX + token;

		
		redisTemplate.opsForValue().set(key, userId, 30, TimeUnit.MINUTES);
		return token;
	}

	// 2. 驗證 Token
	public String verifyToken(String token) {
		String key = RESET_PREFIX + token;
		return redisTemplate.opsForValue().get(key); // 回傳 userId，若無則回傳 null
	}

	// 3. 使用後刪除 Token (確保單次使用)
	public void deleteToken(String token) {
		redisTemplate.delete(RESET_PREFIX + token);
	}
}
