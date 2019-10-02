package com.aias.demo.lock;


/**
 * 分布式锁接口
 */
public interface ILock {
    /**
     * 获取锁
     */
    public void getLock();

    /**
     * 释放锁
     */
    public void unLock();

}
