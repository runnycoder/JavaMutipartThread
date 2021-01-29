package com.icehan.thread.sychronizer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用显示锁 这样锁对象和条件对象相分离
 * 一个锁可以创建多个条件对象 可以对应多个条件谓语
 * @param <T>
 */
public class LockConditionBoundedBuffer<T> {
    protected final Lock lock = new ReentrantLock();
    //条件谓词 notFull(count<items.length)
    private final Condition notFull = lock.newCondition();
    //条件谓词 notEmpty(count>0
    private final Condition notEmpty = lock.newCondition();

    private final int BUFFER_SIZE = 1000;
    private final  T[] itmes = (T[])new Object[BUFFER_SIZE];

    private int tail,head,count;

    /**
     * 阻塞直到 notFull
     * @param x
     */
    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count==itmes.length){
                notFull.await();
            }
            itmes[tail]=x;
            if(++tail==itmes.length){
                tail=0;
            }
            count++;
            //此处使用notEmpty条件谓语的signal()方法 而不实用signalAll()方法
            //是否是因为 只有take()线程会阻塞到notEmpty条件队列中 一次put操作只唤醒一个take 避免唤醒过多的take线程
            //由于循环判断发现队列为空 又陷入等待 减少锁的竞争
            notEmpty.signal();
//            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 阻塞直到 notEmpty
     * @return
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count==0){
                System.out.println(Thread.currentThread().getName()+" in waiting!");
                notEmpty.await();
            }
            T x = itmes[head];
            itmes[head] = null;
            if(++head==itmes.length){
                head=0;
            }
            count--;
            notFull.signal();
//            notFull.signalAll();
            return x;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        LockConditionBoundedBuffer<String> buffer = new LockConditionBoundedBuffer<>();
        Thread thread1 = new Thread(()->{
            System.out.println(Thread.currentThread().getName()+" in running!");
            try {
                String result = buffer.take();
                System.out.println(Thread.currentThread().getName()+" take = "+result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(()->{
            System.out.println(Thread.currentThread().getName()+" in running!");
            try {
                String result = buffer.take();
                System.out.println(Thread.currentThread().getName()+" take = "+result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        try {
            Thread.sleep(3000);
            buffer.put("1");
            Thread.sleep(3000);
            buffer.put("2");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
