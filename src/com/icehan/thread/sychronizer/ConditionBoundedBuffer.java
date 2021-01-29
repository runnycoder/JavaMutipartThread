package com.icehan.thread.sychronizer;

/**
 * 条件等待队列 使用wait notify机制
 */
public class ConditionBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    protected ConditionBoundedBuffer(int capacity) {
        super(capacity);
    }

    /**
     * 阻塞 直到not-full
     * @param v
     * @throws InterruptedException
     */
    public synchronized void put(V v) throws InterruptedException {
        while (isFull())
            wait();
        //当空转为非空事唤醒其它线程
        boolean wasEmpty = isEmpty();
        put(v);
        if(wasEmpty){
            notifyAll();
        }
    }

    /**
     * 阻塞 直到not-empty
     * @return
     * @throws InterruptedException
     */
    public synchronized V take() throws InterruptedException {
        while (isEmpty())
            wait();
        boolean wasFull = isFull();
        V v = take();
        if(wasFull){//满转为非满唤醒其它线程
            notifyAll();
        }
        return v;
    }
}
