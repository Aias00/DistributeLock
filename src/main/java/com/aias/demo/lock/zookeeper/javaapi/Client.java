package com.aias.demo.lock.zookeeper.javaapi;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class Client {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(10);
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                ZookeeperApiDistrbuteLock lock = null;
                try {
                    lock = new ZookeeperApiDistrbuteLock();
                    latch.countDown();
                    latch.await();
                    lock.lock();
                    Thread.sleep(random.nextInt(500));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (lock != null) {
                        lock.unLock();
                    }
                }
            }).start();
        }

    }
}
