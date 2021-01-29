package com.icehan.thread.newlock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockInterruptTest {
    private Lock lock = new ReentrantLock();

    public void insert() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            System.out.println(Thread.currentThread().getName()+"获取了锁");
            long start = System.currentTimeMillis();
            while (true){
                if(System.currentTimeMillis()-start>Integer.MAX_VALUE){
                    break;
                }
            }
        } finally {
            System.out.println(Thread.currentThread().getName()+"执行finally");
            lock.unlock();
            System.out.println(Thread.currentThread().getName()+"释放了锁");
        }
    }

    public static void main(String[] args) {
        LockInterruptTest lockInterruptTest = new LockInterruptTest();
        Runnable r = new Runnable(){
            @Override
            public void run() {
                try {
                    lockInterruptTest.insert();
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+"被中断!");
                }
            }
        };

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("t1 state " +t1.getState());
        System.out.println("t2 state " +t2.getState());
        if(t2.getState().equals(Thread.State.WAITING)){
            t2.interrupt();
        }else{
            t1.interrupt();
        }

    }

}
