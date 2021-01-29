package com.icehan.thread.newlock;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {

    private ArrayList<Integer>  arrayList = new ArrayList<>();
    private Lock lock = new ReentrantLock();
    public void insert(){
        lock.lock();
        try {
            System.out.println(Thread.currentThread().getName()+"得到了锁!");
            for (int i = 0; i < 5; i++) {
                arrayList.add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName()+"释放了锁!");
            lock.unlock();
        }
    }

    public void tryInsert(){
        if(lock.tryLock()){
            try {
                System.out.println(Thread.currentThread().getName()+"获取锁失败!");
                for (int i = 0; i < 5; i++) {
                    arrayList.add(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName()+"释放了锁!");
                lock.unlock();
            }
        }else{
            System.out.println(Thread.currentThread().getName()+"获取锁失败!");
        }
    }

    @Test
    public void test(){
        LockTest lockTest = new LockTest();
        Runnable r = new Runnable(){
            @Override
            public void run() {
                lockTest.insert();
            }
        };
        Thread thread1 = new Thread(r);
        Thread thread2 = new Thread(r);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
            Thread.sleep(1000);
            System.out.println(lockTest.arrayList.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test2(){
        LockTest lockTest = new LockTest();
        Runnable r = new Runnable(){
            @Override
            public void run() {
                lockTest.tryInsert();
            }
        };
        Thread thread1 = new Thread(r);
        Thread thread2 = new Thread(r);
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
            Thread.sleep(1000);
            System.out.println(lockTest.arrayList.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
