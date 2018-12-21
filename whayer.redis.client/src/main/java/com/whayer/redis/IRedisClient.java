package com.whayer.redis;

public interface IRedisClient {

	public boolean set(String key, long value);

	public boolean less(String key, long value);

	public boolean less(String lockKey, String key, long value);

	public long get(String key);
}
