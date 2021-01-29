package com.icehan.thread.semaphore;

import junit.framework.TestCase;

import java.util.Timer;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PutTakeTest extends TestCase {
    private static final ExecutorService pool = Executors.newCachedThreadPool();

    private final AtomicInteger putSum = new AtomicInteger(0);
    private final AtomicInteger takeSum = new AtomicInteger(0);

    //添加计算时间
    private final BarrierTimer timer;

    private final CyclicBarrier barrier;
    private final BoundedBuffer<Integer> buffer;
    private final int nTrials, nPairs;

    PutTakeTest(int capacity, int nPairs, int nTrials) {
        this.buffer = new BoundedBuffer<>(capacity);
        this.nPairs = nPairs;
        this.nTrials = nTrials;
        this.timer = new BarrierTimer();
        this.barrier = new CyclicBarrier(2 * nPairs + 1, timer);
    }

    public void test() {
        timer.clear();
        for (int i = 0; i < nPairs; i++) {
            pool.execute(new Producer());
            pool.execute(new Consumer());
        }
        try {
            barrier.await();//等待所有线程做好准备
            barrier.await();//等待所有线程最终完成
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("putSum=" + putSum.get());
        System.out.println("takeSum=" + takeSum.get());
        long nsPerItem = timer.getTime() / (nPairs * (long) nTrials);
        System.out.println("Throughput: " + nsPerItem + " ns/item");
        assertEquals(takeSum.get(), putSum.get());
    }

    class Producer implements Runnable {
        @Override
        public void run() {
            try {
                int seed = (this.hashCode() ^ (int) System.nanoTime());
                int sum = 0;
                barrier.await();
                for (int i = 0; i < nTrials; i++) {
                    buffer.put(seed);
                    sum += seed;
                    seed = xorShift(seed);
                }
                putSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    class Consumer implements Runnable {
        @Override
        public void run() {
            try {
                barrier.await();
                int sum = 0;
                for (int i = nTrials; i > 0; i--) {
                    sum += buffer.take();
                }
                takeSum.getAndAdd(sum);
                barrier.await();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    static int xorShift(int y) {
        y ^= (y << 6);
        y ^= (y >>> 21);
        y ^= (y << 7);
        return y;
    }

//    public static void main(String[] args) {
//        new PutTakeTest(10, 10, 100000).test();
//        pool.shutdown();
//    }

    public static void main(String[] args) throws InterruptedException {
        int tpt = 100000;    //每个线程测试次数
        for (int cap = 1; cap <= tpt; cap *= 10) {
            System.out.println("Capacity: " + cap);
            for (int pairs = 1; pairs <= 128; pairs *= 2) {
                PutTakeTest t = new PutTakeTest(cap, pairs, tpt);
                System.out.println("Pairs: " + pairs);
                t.test();
                Thread.sleep(1000);
                t.test();
                Thread.sleep(1000);
            }
        }

    }
}