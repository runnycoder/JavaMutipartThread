package com.icehan.thread.sychronizer;

import com.icehan.thread.sychronizer.exception.BufferEmptyException;
import com.icehan.thread.sychronizer.exception.BufferFullException;

import java.nio.BufferOverflowException;

public class GrumpyBoundBuffer<V> extends  BaseBoundedBuffer<V> {
    protected GrumpyBoundBuffer(int capacity) {
        super(capacity);
    }

    public synchronized void put(V v) throws BufferFullException {
        if(isFull()){
            throw new BufferFullException();
        }
        doPut(v);
    }

    public synchronized V take() throws BufferEmptyException {
        if(isEmpty()){
            throw new BufferEmptyException();
        }
        return doTake();
    }

    /**
     * 客户端调用buffer的逻辑
     * 这种忙等待或者自旋等待的方法 不但浪费了大量的cpu资源
     * 而且把队列的状态判断交给了调用方 这样很不好
     * 而且调用方轮询尝试的操作会造成想要实现一个有序队列(FIFO)是很困难的
     * @param args
     */
    public static void main(String[] args) {
        GrumpyBoundBuffer<String> buffer = new GrumpyBoundBuffer<String>(10);
        while(true){
            try {
                String item = buffer.take();
                break;
            } catch (BufferEmptyException e) {
//                Thread.sleep(1000);
                Thread.yield();
            }
        }
    }

}
