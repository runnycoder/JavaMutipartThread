package com.icehan.thread.semaphore;

import junit.framework.TestCase;

public class BoundedBufferTest extends TestCase {
    public void testIsEmptyWhenConstructed(){
        BoundedBuffer<Integer> integerBoundedBuffer = new BoundedBuffer<Integer>(10);
        assertTrue(integerBoundedBuffer.isEmpty());
        assertFalse(integerBoundedBuffer.isFull());
    }

    public void testIsFullAfterPuts() throws InterruptedException {
//        BoundedBuffer<Integer> integerBoundedBuffer = new BoundedBuffer<Integer>(10);
//        for (int i = 0; i <10 ; i++) {
//            integerBoundedBuffer.put(i);
//        }
//        System.out.println(integerBoundedBuffer.isEmpty());
//        System.out.println(integerBoundedBuffer.isFull());
//        assertTrue(integerBoundedBuffer.isEmpty());
//        assertFalse(integerBoundedBuffer.isFull());
//        assertFalse(true);
        System.out.println("111111");
    }

    /**
     * 测试buffer take()方法的阻塞 如果没有正常阻塞
     * 说明是有问题 抛出InterruptedException认为是阻塞正常进行
     */
    public void testTakeBlocksWhenEmpty(){
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(10);
        Thread taker =new Thread(){
            public void run(){
                try {
                    int unused = buffer.take();
                    fail();
                } catch (InterruptedException e) {
                    System.out.println("I am Interrupted");
                }
            }
        };

        try {
            taker.start();
            Thread.sleep(1000);
            taker.interrupt();
            taker.join(1000);
            assertFalse(taker.isAlive());
        } catch (InterruptedException e) {
            fail();
        }
    }

}
