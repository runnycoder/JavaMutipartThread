package com.icehan.thread.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    public static void main(String[] args) {
        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(20);
        //创建线程池 池中保存的线程数为3 允许的最大线程数为5
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 5, 50,TimeUnit.MILLISECONDS, arrayBlockingQueue );

        for (int i = 0; i < 10; i++) {
            MyThread myThread = new MyThread();
            threadPoolExecutor.execute(myThread);
        }

        threadPoolExecutor.shutdown();

    }
}

class MyThread implements Runnable{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" is running!");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
