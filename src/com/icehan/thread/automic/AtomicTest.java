package com.icehan.thread.automic;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;

public class AtomicTest {

    @Test
    public void test(){
           AtomicLong atomicLong = new AtomicLong();
           atomicLong.accumulateAndGet(111, Math::max);
    }

    @Test
    public void test1(){
        LongAdder longAdder = new LongAdder();
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(21);
        for (int i = 0; i <20 ; i++) {
                executorService.submit(()->{
                    for (int j = 0; j < 10 ; j++) {
                        longAdder.add(2);
                    }
                    countDownLatch.countDown();
                });
        }
        try {
            countDownLatch.countDown();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(longAdder.sum());
        executorService.shutdown();

    }

    @Test
    public void test2(){
        LongAccumulator longAccumulator = new LongAccumulator((a,b)->a*b,1);
        ExecutorService executorService = Executors.newCachedThreadPool();
        CountDownLatch countDownLatch = new CountDownLatch(5);
        for (int i = 0; i <4 ; i++) {
            executorService.submit(()->{
                for (int j = 0; j < 5 ; j++) {
                    longAccumulator.accumulate(2);
                }
                countDownLatch.countDown();
            });
        }
        try {
            countDownLatch.countDown();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(longAccumulator.get());
        executorService.shutdown();

        System.out.println(Math.pow(1024, 2));
    }
}
