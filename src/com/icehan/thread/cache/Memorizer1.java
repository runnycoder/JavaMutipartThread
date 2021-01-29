package com.icehan.thread.cache;

import java.util.HashMap;
import java.util.Map;

public class Memorizer1<A,V> implements Computable<A,V> {
    private final Map<A,V> cache = new HashMap<>();

    private final Computable<A,V> computable;

    public Memorizer1(Computable<A,V> computable) {
        this.computable = computable;
    }

    /**
     * 第一种实现方式
     * 由于HashMap不是线程安全的 所以使用的外部的同步锁
     * 这样带来了一个问题 每次只能有一个线程获取到锁 其他线程则处于阻塞之中
     * 如果compute方法耗时很长 那么其它线程只能等待计算完成 吞吐量很低
     * @param arg
     * @return
     * @throws InterruptedException
     */
    @Override
    public synchronized V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if(null==result){
            result = computable.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
