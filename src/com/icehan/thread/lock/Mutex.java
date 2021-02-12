package com.icehan.thread.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Mutex implements Lock,java.io.Serializable {

    private static class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean isHeldExclusively(){
            return getState()==1;
        }

        public boolean tryAcquire(int acquires){
            assert acquires == 1;//这里限定只能为1
            if(compareAndSetState(0, 1)){//state为0才设置为1 不可重入
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int release){
            assert release == 1;
            if(getState()==0){//如果没有占有状态抛出非法操作异常
                throw new IllegalMonitorStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return false;
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        return null;
    }



    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        final int[] ints = new int[1];
        ints[0]=0;
        CountDownLatch countDownLatch = new CountDownLatch(6);
        Mutex mutex = new Mutex();
        for (int i = 0; i < 5 ; i++) {
            executorService.submit(()->{
                mutex.lock();
                try{
                    for (int j = 0; j < 10 ; j++) {
                            ints[0]+=1;

                    }
                    countDownLatch.countDown();
                }finally {
                    mutex.unlock();
                }
            });
        }
        countDownLatch.countDown();
        countDownLatch.await();
        System.out.println(ints[0]);
        executorService.shutdown();
    }

}
