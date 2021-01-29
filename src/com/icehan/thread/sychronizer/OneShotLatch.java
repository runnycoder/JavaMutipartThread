package com.icehan.thread.sychronizer;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 使用AbstractQueuedSynchronizer(AQS)实现一个闭锁
 */
public class OneShotLatch {
    private final Sync sync = new Sync();

    public void signal(){
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    private class Sync extends AbstractQueuedSynchronizer{

        //如果锁打开成功(state=1)
        @Override
        protected int tryAcquireShared(int args){
            return (getState()==1)?1:-1;
        }

        protected boolean tryReleaseShared(int ignored){
            setState(1);//闭锁现在已经打开
            return true;
        }
    }

    public static void main(String[] args) {
        OneShotLatch oneShotLatch = new OneShotLatch();
        try {
            System.out.println("main start!");
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+" in run!");
                try {
                    oneShotLatch.await();
                    System.out.println(Thread.currentThread().getName()+" run over");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();


            Thread.sleep(3000);
            oneShotLatch.signal();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
