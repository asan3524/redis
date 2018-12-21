package com.whayer.redisson;

import java.util.concurrent.TimeUnit;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import com.whayer.redis.IRedisClient;

public class RedissonClientImpl implements IRedisClient {

	private static Config config;

	public RedissonClientImpl(String address, String password) {
		config = new Config();
		config.useSingleServer().setAddress(address).setConnectionPoolSize(100).setTimeout(1000);
	}

	@Override
	public boolean set(String key, long value) {
		// TODO Auto-generated method stub
		RedissonClient redissonClient = Redisson.create(config);
		redissonClient.getAtomicLong(key).set(value);
		redissonClient.shutdown();
		return true;
	}

	@Override
	public boolean less(String key, long value) {
		// TODO Auto-generated method stub
		RedissonClient redissonClient = Redisson.create(config);
		long stock = redissonClient.getAtomicLong(key).get();
		if (stock > 0) {
			redissonClient.getAtomicLong(key).set(stock - value);
		}
		redissonClient.shutdown();
		return true;
	}

	@Override
	public boolean less(String lockKey, String key, long value) {
		// TODO Auto-generated method stub
		RedissonClient redissonClient = Redisson.create(config);
		RLock lock = redissonClient.getLock(lockKey);
		// 设置60秒自动释放锁 （默认是30秒自动过期）
		// lock.lock(60, TimeUnit.SECONDS);
		try {
			boolean result = lock.tryLock(5, 5, TimeUnit.SECONDS);
			System.out.println("lock get " + result);
			if (result) {
				long stock = redissonClient.getAtomicLong(key).get();
				if (stock > 0) {
					redissonClient.getAtomicLong(key).set(stock - value);
					Long id = Thread.currentThread().getId();
					System.out.println("Test method executing on thread with id: " + id + " test_:lockkey:" + lockKey + ",stock:" + (stock - value) + "");
				}

				lock.forceUnlock();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 释放锁
		// lock.unlock();
		redissonClient.shutdown();
		return true;
	}
	
	@Override
	public long get(String key) {
		// TODO Auto-generated method stub
		RedissonClient redissonClient = Redisson.create(config);
		long result = redissonClient.getAtomicLong(key).get();
		redissonClient.shutdown();
		return result;
	}
}
