package com.fymod.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

	@Autowired private StringRedisTemplate template;
	
	/**
	 * 保存永久数据
	 */
	public void setString(String key, String content) {
		template.opsForValue().set(key, content);
	}
	
	/**
	 * 保存数据指定的时间，单位是秒
	 */
	public void setString(String key, String content, int seconds) {
		template.opsForValue().set(key, content, seconds, TimeUnit.SECONDS);
	}
	
	/**
	 * 读取数据
	 */
	public String getString(String key) {
		return template.opsForValue().get(key);
	}
	
}
