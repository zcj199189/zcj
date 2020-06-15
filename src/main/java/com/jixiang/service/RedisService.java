package com.jixiang.service;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
@Service
public class RedisService {

	@Resource(name = "redisTemplate")
	StringRedisTemplate redis;
	public void set(String key, String value, long timeout, TimeUnit unit) {
		redis.opsForValue().set(key, value, timeout, unit);
	}
	public String get(String key) {
		return redis.opsForValue().get(key);
	}
	public void delete(String key) {
		redis.delete(key);
	}
}
