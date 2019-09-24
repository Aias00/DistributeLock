package com.aias.demo;


import org.I0Itec.zkclient.IZkDataListener;

import java.util.concurrent.CountDownLatch;

/**
 * 分布式锁的第一种实现方式
 */
public class ZookeeperDistrbuteLock extends ZookeeperAbstractLock {

    private CountDownLatch countDownLatch = null;

    @Override
    boolean tryLock() {
        try {
            // 创建一个临时节点
            zkClient.createEphemeral(PATH);
            // 如果创建成功  返回true
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 监测这个路径是否存在
     */
    @Override
    void waitLock() {
        IZkDataListener iZkDataListener = new IZkDataListener() {
            public void handleDataChange(String dataPath, Object data) throws Exception {

            }

            public void handleDataDeleted(String dataPath) throws Exception {
                // 唤醒等待的线程
                if (countDownLatch != null) {
                    countDownLatch.countDown();
                }
            }
        };
        // 注册事件
        zkClient.subscribeDataChanges(PATH, iZkDataListener);
        if (zkClient.exists(PATH)) {
            countDownLatch = new CountDownLatch(1);
        }

    }

    @Override
    public void unLock() {
        if (null != zkClient) {
            // 删除路径
            zkClient.delete(PATH);
            // 释放资源
            zkClient.close();
            System.out.println("释放锁资源...");
        }
    }
}
