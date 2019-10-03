package com.aias.demo.lock.zookeeper.javaapi;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZookeeperApiDistrbuteLock {

    private static final String ROOT_LOCKS = "/LOCKS";

    private ZooKeeper zooKeeper;
    /**
     * 会话超时时间
     */
    private int sessionTimeOut = 5000;
    /**
     * 记录锁节点id
     */
    private String lockID;

    /**
     * 节点存储的数据
     */
    private final static byte[] data = {1, 2};

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZookeeperApiDistrbuteLock() throws IOException, InterruptedException {
        this.zooKeeper = ZookeeperClient.getInstance();
        this.sessionTimeOut = ZookeeperClient.sessionTimeOut;
    }

    /**
     * 获取锁的方法
     *
     * @return
     */
    public synchronized boolean lock() {
        try {
            lockID = zooKeeper.create(ROOT_LOCKS + "/", data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(Thread.currentThread().getName() + "成功创建了lock节点,节点Id:" + lockID + ",开始竞争锁");
            // 获取根节点下的所有节点
            List<String> childrenNodes = zooKeeper.getChildren(ROOT_LOCKS, true);
            // 从小到大排序
            SortedSet<String> sortedSet = new TreeSet<>();
            for (String childrenNode : childrenNodes) {
                sortedSet.add(ROOT_LOCKS + "/" + childrenNode);
            }
            // 拿到最小的节点
            String first = sortedSet.first();
            if (lockID.equals(first)) {
                // 表示当前节点就是最小节点
                System.out.println(Thread.currentThread().getName() + "-->成功获取到锁，lock节点为：" + lockID);
                return true;
            }
            SortedSet<String> lessThanLockId = sortedSet.headSet(lockID);
            if (!lessThanLockId.isEmpty()) {
                // 拿到比当前LockId这个节点更小的上一个节点
                String prevLockId = lessThanLockId.last();
                zooKeeper.exists(prevLockId, new LockWatcher(countDownLatch));
                countDownLatch.await(sessionTimeOut, TimeUnit.MICROSECONDS);
                // 上面这段代码意味着如果节点被删除或者超时了
                System.out.println(Thread.currentThread().getName() + "成功获取锁：" + lockID);
            }
            return true;
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 释放锁的操作
     *
     * @return
     */
    public synchronized boolean unLock() {
        System.out.println(Thread.currentThread().getName() + "开始释放锁：" + lockID);
        try {
            zooKeeper.delete(lockID, -1);
            System.out.println("节点：" + lockID + "被删除");
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return false;
    }


}
