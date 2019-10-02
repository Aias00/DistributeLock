package com.aias.demo.lock.zookeeper;


import org.I0Itec.zkclient.IZkDataListener;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * zookeeper分布式锁第二种方式
 * 基于相互监听
 * 可以理解为排队，在最前面的获得锁（比较占资源）
 */
public class ZookeeperDistrbuteLock2 extends ZookeeperAbstractLock {

    private String beforePath;
    private String currentPath;
    private CountDownLatch countDownLatch = null;

    public ZookeeperDistrbuteLock2() {
        if (!this.zkClient.exists(PATH2)) {
            this.zkClient.createPersistent(PATH2);
        }
    }


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
        // 给排在前面的节点增加数据删除的watcher，本质是启动另外一个线程去监听前置节点
        zkClient.subscribeDataChanges(PATH, iZkDataListener);
        if (zkClient.exists(beforePath)) {
            countDownLatch = new CountDownLatch(1);
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.zkClient.subscribeDataChanges(beforePath, iZkDataListener);

    }

    @Override
    boolean tryLock() {
        // 如果currentpath为空则为第一次尝试加锁，第一次加锁赋值currentPath
        if (null == currentPath || currentPath.length() <= 0) {
            // 创建一个临时顺序节点
            currentPath = this.zkClient.createEphemeralSequential(PATH2 + '/', "lock");
        }
        // 获取所有临时节点并排序，临时节点的名称为自增长的字符串如：0000000400
        List<String> childrens = this.zkClient.getChildren(PATH2);
        Collections.sort(childrens);
        // 如果当前节点在所有节点中排名第一则获取锁成功
        if (currentPath.equals(PATH2 + '/' + childrens.get(0))) {
            return true;
        } else {
            // 如果不是排名第一，则获取前面的节点名称，并赋值给beforePath
            int wz = Collections.binarySearch(childrens, currentPath.substring(7));
            beforePath = PATH2 + '/' + childrens.get(wz - 1);
        }
        return false;
    }

    @Override
    public void unLock() {
        if (null != this.zkClient) {
            // 删除当前临时节点
            this.zkClient.delete(currentPath);
        }
    }
}
