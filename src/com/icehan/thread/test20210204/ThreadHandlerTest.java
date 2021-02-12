package com.icehan.thread.test20210204;

import org.junit.Test;

public class ThreadHandlerTest {

    /**
     * 1 ) 如果该线程组有父线程组， 那么父线程组的 uncaughtException 方法被调用。
     * 2 ) 否则， 如果 Thread.getDefaultExceptionHandler 方法返回一个非空的处理器， 则调用该处理器。
     * 3 ) 否则，如果 Throwable 是 ThreadDeath 的一个实例， 什么都不做。
     * 4 ) 否则，线程的名字以及 Throwable 的栈轨迹被输出到 System.err 上。这是你在程序中肯定看到过许多次的栈轨迹。
     */
    @Test
    public void test(){
        Thread t1 = new Thread(() -> {
            try {
                int i = 1/0;
            }catch (Exception e){
                throw e;
//                throw new ThreadDeath();
            }
        });
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                System.out.println(Thread.currentThread().getName()+"=========="+e.getCause());
                e.printStackTrace();
            }
        };
        t1.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        t1.start();
    }
}
