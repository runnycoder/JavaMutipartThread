package com.icehan.thread.future;

import junit.framework.TestCase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TestThreadFactory extends TestCase implements ThreadFactory {
    private final AtomicInteger numCreated = new AtomicInteger();
    private final ThreadFactory factory = Executors.defaultThreadFactory();
    @Override
    public Thread newThread(Runnable r) {
        numCreated.incrementAndGet();
        return factory.newThread(r);
    }

    /**
     * 测试线程池的动态扩展
     * @throws InterruptedException
     */
    public void testPoolExpansion() throws InterruptedException{
        int MAX_SIZE = 10;
        TestThreadFactory testThreadFactory = new TestThreadFactory();
        ExecutorService executorService = Executors.newFixedThreadPool(10, testThreadFactory);
        for (int i = 0; i < 10*MAX_SIZE; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(Long.MAX_VALUE);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        for (int i = 0; i < 20&& testThreadFactory.numCreated.get()<MAX_SIZE; i++) {
            Thread.sleep(100);
        }
        assertEquals(testThreadFactory.numCreated.get(), MAX_SIZE);
        executorService.shutdown();
    }

}
