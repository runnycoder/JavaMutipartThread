package com.icehan.thread.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Memorizer2<A, V> implements Computable<A, V> {
    private final Map<A,V> cache = new ConcurrentHashMap<A,V>();
    private final Computable<A,V> computable;

    public Memorizer2(Computable<A, V> computable) {
        this.computable = computable;
    }

    /**
     * 使用线程安全的ConcurrentHashMap get和put操作保证了操作的原子性
     * 但同时还有另一个问题 假设compute方法的计算周期特别长
     * thread1首次判断cache中没有数据 开始进行compute计算
     * thread2进入方法并不知道thread1正在进行计算操作 就会导致compute方法执行两次
     * @param arg
     * @return
     * @throws InterruptedException
     */
    @Override
    public V compute(A arg) throws InterruptedException {
        V result = cache.get(arg);
        if(null==result){
            result = computable.compute(arg);
            cache.put(arg, result);
        }
        return result;
    }
}
