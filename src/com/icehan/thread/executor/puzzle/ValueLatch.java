package com.icehan.thread.executor.puzzle;

import java.util.concurrent.CountDownLatch;
//可携带结果的闭锁
public class ValueLatch<T> {
    private T value = null;
    private final CountDownLatch done = new CountDownLatch(1);

    public boolean isSet(){
        return done.getCount()==0;
    }

    public synchronized void setValue(T newValue){
        if(!isSet()){
            value = newValue;
            done.countDown();
        }
    }

    public T getValue() throws InterruptedException {
        done.await();
        synchronized (this){
            return  value;
        }
    }
}
