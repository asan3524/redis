package com.whayer.redisson;

import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.whayer.redis.IRedisClient;

public class RedissonClientTest {

	private static String address = "redis://192.168.40.161:30379";
	private static String password = "123456";
	private static IRedisClient redisClient;

	private static String lockKey = "testLock";
	private static String key = "test";
	private static long value = 1000;

	@BeforeTest
	public static void init() {
		redisClient = new RedissonClientImpl(address, password);
		redisClient.set(key, value);
	}

	@Test(threadPoolSize = 100, invocationCount = 60)
	public void testLock1() {
		for (int i = 0; i < 20; i++) {
			redisClient.less(lockKey, key, 1);
		}
		Assert.assertEquals(1, 1);
	}

	@Test(dependsOnMethods = "testLock1")
	public void testLock2() {
		Assert.assertEquals(redisClient.get(key), 0);
	}
}
