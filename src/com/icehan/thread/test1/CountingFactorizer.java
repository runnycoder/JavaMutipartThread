package com.icehan.thread.test1;

import java.util.concurrent.atomic.AtomicLong;

public class CountingFactorizer {
    private final AtomicLong count = new AtomicLong();

    public long getCount(){
        return count.get();
    }

    public long increase(){
        return count.incrementAndGet();
    }

    public static void main(String[] args) throws InterruptedException {
        CountingFactorizer countingFactorizer = new CountingFactorizer();
        new Thread(new Runnable(){

            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    countingFactorizer.increase();
                }
            }
        }).start();

        new Thread(new Runnable(){

            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    countingFactorizer.increase();
                }
            }
        }).start();
        Thread.sleep(10000);
        System.out.println(countingFactorizer.getCount());
    }
}
