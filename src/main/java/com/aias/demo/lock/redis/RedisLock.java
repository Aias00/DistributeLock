package com.aias.demo.lock.redis;

import com.aias.demo.lock.ILock;
import org.springframework.data.redis.core.StringRedisTemplate;

public class RedisLock implements ILock {
    /**
     * redis client
     */
    private static StringRedisTemplate redisTemplate;
    /**
     * 锁的键值
     */
    private String lockKey;
    /**
     * 锁超时, 防止线程得到锁之后, 不去释放锁
     */
    private int expireMsecs = 15 * 1000;
    /**
     * 锁等待, 防止线程饥饿
     */
    private int timeoutMsecs = 15 * 1000;
    /**
     * 是否已经获取锁
     */
    private boolean locked = false;

    public RedisLock(String lockKey) {
        this.lockKey = lockKey;
    }

    public RedisLock(String lockKey, int timeoutMsecs) {
        this.lockKey = lockKey;
        this.timeoutMsecs = timeoutMsecs;
    }

    public RedisLock(String lockKey, int expireMsecs, int timeoutMsecs) {
        this.lockKey = lockKey;
        this.expireMsecs = expireMsecs;
        this.timeoutMsecs = timeoutMsecs;
    }
    public String getLockKey() {
        return this.lockKey;
    }

    @Override
    public void getLock() {

    }

    @Override
    public void unLock() {

    }
}
