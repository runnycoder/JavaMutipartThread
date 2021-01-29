package com.icehan.thread.sychronizer;

/**
 * 利用轮询加休眠实现拙劣的阻塞
 * 轮询休眠的这种模式可以允许调用者在等待超时的时候显示的中断
 * @param <V>
 */
public class SleepyBoundedBuffer<V> extends BaseBoundedBuffer<V> {
    protected SleepyBoundedBuffer(int capacity) {
        super(capacity);
    }

    public void put(V v) throws InterruptedException {
        while (true){
            synchronized (this){
                if(!isFull()){
                    doPut(v);
                    return;
                }
            }
            Thread.sleep(1000);
        }
    }

    public V take() throws InterruptedException {
        while (true){
            synchronized (this){
                if(!isEmpty()){
                    return doTake();
                }
            }
            Thread.sleep(1000);
        }
    }
}
