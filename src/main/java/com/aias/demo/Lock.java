package com.aias.demo;


/**
 * 分布式锁接口
 */
public interface Lock {
    /**
     * 获取锁
     */
    public void getLock();

    /**
     * 释放锁
     */
    public void unLock();

}
