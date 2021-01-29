package com.icehan.thread.sychronizer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock和semaphore有很多相似的特性 他们都用到一个共同的基类
 * AbstractQueueSynchronizer(AQS)这是一个用来构建锁的Synchronizer框架
 * 还有CountDownLatch,ReentrantReadWriteLock,SynchronousQueue和FutureTask都是基于此构建的
 * 下面我们使用lock 实现一个semaphore 当然这并不是java.util.concurrent.Semaphore的真正实现
 */
public class SemaphoreOnLock {
    private final ReentrantLock lock  = new ReentrantLock();
    //条件谓词 permitsAvailable(permits>0)
    private final Condition permitsAvailable = lock.newCondition();
    private int permits;

    public SemaphoreOnLock(int permits) {
        lock.lock();
        try {
            this.permits = permits;
        } finally {
            lock.unlock();
        }
    }

    //阻塞 直到permitsAvailable条件满足
    public void acquire() throws InterruptedException {
        lock.lock();
        try {
            while (permits<=0){
                permitsAvailable.await();
            }
            --permits;
        } finally {
            lock.unlock();
        }
    }

    public void release(){
        lock.lock();
        try {
            ++permits;
            permitsAvailable.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        Object obj = new Object();
        System.out.println("obj address "+obj);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        System.out.println(countDownLatch);
        Runnable task = ()->{
            System.out.println(Thread.currentThread().getName()+" in run!" + obj);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            countDownLatch.countDown();
            System.out.println(countDownLatch);
        };
//        obj = new Object();
        executor.execute(task);
        executor.execute(task);
        try {
            System.out.println("main is waiting");
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main run over");

        executor.shutdown();
    }



}
