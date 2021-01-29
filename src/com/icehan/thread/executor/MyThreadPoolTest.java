package com.icehan.thread.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class MyThreadPoolTest extends ThreadPoolExecutor {
    private final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private final Logger log = Logger.getLogger("MyThreadPoolTest");
    private final AtomicLong numTasks = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public MyThreadPoolTest(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected void beforeExecute(Thread t,Runnable r){
        super.beforeExecute(t, r);
        log.info(String.format("Thread %s: start %s", t,r));
        startTime.set(System.nanoTime());
    }

    protected void afterExecute(Runnable r,Throwable t){
        try {
            long endTime = System.nanoTime();
            long taskTime = endTime - startTime.get();
            numTasks.incrementAndGet();
            totalTime.addAndGet(taskTime);
            log.info(String.format("Thread %s: end %s, time=%dns", t,r,taskTime));
        } finally {
            super.afterExecute(r, t);
        }
    }

    protected void terminated(){
        try {
            log.info(String.format("Terminated: avg time=%dns", totalTime.get()/numTasks.get()));
        } finally {
            super.terminated();
        }
    }

    public static void main(String[] args) {
        ArrayBlockingQueue<Runnable> arrayBlockingQueue = new ArrayBlockingQueue<>(20);
        MyThreadPoolTest myThreadPoolTest = new MyThreadPoolTest(3, 5, 60, TimeUnit.MILLISECONDS, arrayBlockingQueue);

        for (int i = 0; i <10 ; i++) {
            myThreadPoolTest.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    System.out.println(Thread.currentThread().getName()+"is run");
                }
            });
        }
        myThreadPoolTest.shutdown();
    }
}
