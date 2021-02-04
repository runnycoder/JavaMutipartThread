package com.icehan.thread.test20210204;

import org.junit.Test;

/**
 * threadObj.join()方法的作用是可以阻塞当前线程直到threadObj线程运行完毕
 * 一直很好奇是怎么实现的
 */
public class ThreadJoinTest {

    @Test
    public  void test() {
        long start = System.nanoTime();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName()+"in run");
            //sleep 3 seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"run over!");
        });
        t1.start();
        try {
            /**
             * 点进去查看join的源码 忽略带有超时时间的部分
             * 首先join方法是synchronized修饰的 锁对象是调用者t1
             * 关键的地方在于
             *    while (isAlive()) {
             *                 wait(0);
             *    }
             *    this.isAlive()检测的是对象是否处于就绪状态先不看循环只看wait(0)部分
             *    wait()方法只能用于同步区域内 作用是让当前线程阻塞 直到锁对象调用notify()或者notifyAll()
             *    进行唤醒 这里锁对象=线程对象t1 阻塞的是线程main而不是t1这点要注意(因为方法调用是在主线程中的 t1线程的内容是run方法)
             *    所以阻塞主线程这件事很好理解 主要明确的是 线程阻塞的锁对象(监听器对象)是线程t1
             *    那么主线程是怎么做到等到t1线程执行完毕才被唤醒的呢？
             *    join方法的注释上有一段话
             *    As a thread terminates the
             *    {@code this.notifyAll} method is invoked. It is recommended that
             *    applications not use {@code wait}, {@code notify}, or
             *    {@code notifyAll} on {@code Thread} instances.
             *    当线程运行结束的时候会调用this.notifyAll()方法(notifyAll()不是Thread类的方法而是Object类的方法)
             *    也不推荐在Thread类的实例对象上调用wait(),notify(),notifyAll()这些方法
             *    也就是说main线程阻塞在锁(t1)上,当t1线程的方法运行结束后会调用t1.notifyAll()唤醒在t1锁对象上阻塞的所有线程
             *    包括main线程 ok到这里差不多把join的阻塞机制想明白了
             *    还有一个疑问?
             *    while (isAlive()) {
             *         wait(0);
             *    }
             *    为什要循环判断t1线程的就绪状态？我们看第二个测试
             *
             */
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long end = System.nanoTime();
        System.out.println("main wait "+(end-start));

    }

    @Test
    public  void test2() {
        long start = System.nanoTime();
        Thread t1 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName()+"in run");
            //sleep 3 seconds
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName()+"run over!");
        });
        System.out.println("t1 isAlive="+t1.isAlive());//输出一下此时线程的活跃状态
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /**
         * 注意这里  test2和test1唯一的区别就是将t1.start放到了t1.join的后面
         * 执行会发现main线程根本不会等待t1线程的执行
         * 因为对于一个新建的线程 不调用start线程并不是可运行的状态
         * while (isAlive()) {//此处的循环判断直接跳过等待了 所以主线程main并不会等待t1
         *      wait(0);
         * }
         * 那为什么不用if判断呢？我个人认为如果对于完全不在其它地方使用t1作为锁对象的情况下应该是可行的
         * 所以才会有这句话
         * It is recommended that
         * applications not use {@code wait}, {@code notify}, or
         * {@code notifyAll} on {@code Thread} instances.
         * 所以不推荐使用Thread的实例作为对象
         */
        t1.start();
        System.out.println("t1 isAlive="+t1.isAlive());//输出一下此时线程的活跃状态
        long end = System.nanoTime();
        System.out.println("main wait "+(end-start));

    }


    @Test
    public  void test3() {
        long start = System.nanoTime();
        Object lock1 = new Object();
        Thread t1 = new Thread(() -> {
            System.out.println("t1 in run");
            while (!Thread.currentThread().isInterrupted()){
            }
            System.out.println("t1 interrupted!");
            System.out.println("t1=== is alive="+Thread.currentThread().isAlive());
//            Thread.currentThread().suspend();
        });

        Thread t2 = new Thread(() -> {
            System.out.println("t2 in run");
            try {
                Thread.sleep(1000);//等一会等待主线程阻塞到t1上
                synchronized (t1){
                    System.out.println("t2====t1 notifyAll threads");
                    t1.stop();//通过stop的方式杀死t1线程 结束主线程的阻塞
//                    t1.notifyAll(); 如果使用notifyAll()的方式就算是唤醒了main线程
//                    还会在while循环里面重新判断t1线程的状态如果t1.isAlive()=true main线程会重新进入等待
                }
                Thread.sleep(1000);
                System.out.println("t2====t1 is alive="+t1.isAlive());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("t2 run over");
        });


        t1.start();

        try {
            Thread.sleep(100);//确保t1启动
            System.out.println("main in lock1");
            t2.start();
            t1.join();
            System.out.println("t1 is alive = "+t1.isAlive());
            System.out.println("main free from lock1");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.nanoTime();
        System.out.println(end-start);
    }
}
