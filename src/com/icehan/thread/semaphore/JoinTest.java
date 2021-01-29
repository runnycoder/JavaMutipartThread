package com.icehan.thread.semaphore;

import org.junit.Test;

/**
 * join方法为什么可以让线程等待呢
 */
public class JoinTest {

    static class LocalThread extends Thread {
        private Object lock;

        public void setLock(Object lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName()+"in run!");
            synchronized (lock) {
                System.out.println(Thread.currentThread().getName()+"holding lock");
                for (int i = 0; i < 100; i++) {
                    System.out.println(Thread.currentThread().getName() + "is running -" + i);
                }
//                try {
//                    System.out.println(Thread.currentThread().getName()+"waiting");
//                    wait();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    @Test
    public void test1(){
        Object lock = new Object();//使用此锁无法继续执行run方法 因为需要等待lock
        //但同样的说明join()方法并不会让main线程释放synchronized持有的 lock锁
        LocalThread thread1 = new LocalThread();
        thread1.setLock(lock);
        thread1.start();
        synchronized (lock){
            for (int i = 0; i < 22; i++) {
                if(i==20){
                    try {
                        thread1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName()+"running -"+i);
            }
        }
    }

    /**
     * 那么join()方法是怎么让主线程进行等待的呢
     * 此时锁对象设置为子线程对象本身 一开始main()方法持有线程锁thread1 thread1 run()方法阻塞
     * 循环到第20次时 join()方法执行 join方法里面使用了wait()主线程wait过程中释放锁 thread1 run()方法继续执行
     * 说明了thread1获取到了main线程释放的线程锁 这是在显式使用synchronized关键词持有锁的情况
     * 下面看看不加同步锁会发生什么
     */
    @Test
    public void test2(){

        LocalThread thread1 = new LocalThread();
        thread1.setLock(thread1);
        thread1.start();
        synchronized (thread1){
            for (int i = 0; i < 22; i++) {
                if(i==20){
                    try {
                        thread1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName()+"running -"+i);
            }
        }
    }

    /**
     * 此处与test2唯一的不同就是main线程没有加上同步标识
     * 在循环的前20次 两个线程交替运行 但是在join()方法执行完毕之后
     * main线程必须要等待子线程执行完毕
     */
    @Test
    public void test3(){
        LocalThread thread1 = new LocalThread();
        thread1.setLock(thread1);
        thread1.start();
//        synchronized (thread1){
            for (int i = 0; i < 22; i++) {
                if(i==20){
                    try {
                        thread1.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(Thread.currentThread().getName()+"running -"+i);
            }
//        }
    }

    public static void main(String[] args) {
    }

}
