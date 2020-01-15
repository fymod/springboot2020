package com.fymod.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

	@Autowired private RedisUtil redisUtil;
	
	
	@GetMapping("/set1")
	public boolean setKey1(String content) {
		redisUtil.setString("key1", content);
		return true;
	}
	
	@GetMapping("/set2")
	public boolean setKey2(String content) {
		redisUtil.setString("key2", content, 30);
		return true;
	}
	
	@GetMapping("/get")
	public String get(String key) {
		return redisUtil.getString(key);
	}
	
}
