package com.aias.demo;

import org.I0Itec.zkclient.ZkClient;

/**
 * 将重复代码写进抽象类
 */
public abstract class ZookeeperAbstractLock implements Lock{
    /**
     * zookeeper连接地址
     */
    private static final String CONNECTIONSTRING = "127.0.0.1:2181";

    protected ZkClient zkClient = new ZkClient(CONNECTIONSTRING);

    protected static final String PATH = "/lock";
    protected static final String PATH2 = "/lock2";

    public void getLock() {
        if(tryLock()){
            System.out.println("## 获取lock锁的资源 ##");
        }else{
            // 等待
            waitLock();
            // 重新获取锁资源
            getLock();
        }
    }

    abstract void waitLock();

    abstract boolean tryLock();

    abstract public void unLock();
}
