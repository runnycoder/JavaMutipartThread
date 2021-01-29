package com.icehan.thread.cache;

import java.util.Map;
import java.util.concurrent.*;

public class Memorizer3<A,V> implements Computable<A,V> {
    private final Map<A, Future<V>> cache = new ConcurrentHashMap<A,Future<V>>();
    private final Computable<A,V> computable;

    public Memorizer3(Computable<A, V> computable) {
        this.computable = computable;
    }

    /**
     * 使用Future任务形式 首先检查计算是否已经开始
     * 如果没有就创建一个FutureTask并将它注册到Map中开始计算
     * 如果已经存在future.get()就会等待正在进行的计算获取结果 避免了多次计算的运算量
     * 这种方式唯一的问题就是vFuture的获取和判断是非原子性的 在多线程环境下可能会同时存在两个线程获取vFuture==null
     * 开始计算操作
     * @param arg
     * @return
     * @throws InterruptedException
     */
    @Override
    public V compute(A arg) throws InterruptedException {
        Future<V> vFuture = cache.get(arg);
        if(null == vFuture){
            Callable<V> eval =new Callable<V>(){
                @Override
                public V call() throws Exception {
                    return computable.compute(arg);
                }
            };
            FutureTask<V> vFutureTask = new FutureTask<>(eval);
            vFuture = vFutureTask;
            cache.put(arg, vFutureTask);
            vFutureTask.run();
        }
        try {
            return vFuture.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 由于ConcurrentMap的操作是原子性的使用了内部锁
     * 所以我们想要实现判断和操作的原子性 不能依靠外部同步加锁的方式(外部加锁和ConcurrentMap内部锁不是同一个 参见ConcurrentHashMapTest)
     * 所以只能依靠map的putIfAbsent方法进行双重判断
     * @param arg
     * @return
     * @throws InterruptedException
     */
    public V compute2(A arg) throws InterruptedException {
        Future<V> vFuture = cache.get(arg);
        if(null == vFuture){
            Callable<V> eval =new Callable<V>(){
                @Override
                public V call() throws Exception {
                    return computable.compute(arg);
                }
            };
            FutureTask<V> vFutureTask = new FutureTask<>(eval);
            vFuture = cache.putIfAbsent(arg, vFutureTask);//如果arg不存在 则把vFutureTask放进去返回null 如果存在返回现有对象
            if(null == vFuture){//为null说明此时任务未执行
                vFuture = vFutureTask;
                vFutureTask.run();
            }

        }
        try {
            return vFuture.get();
        } catch (ExecutionException e) {
            cache.remove(arg, vFuture);//执行出现异常则从缓存中移除此future任务
            e.printStackTrace();
        }
        return null;
    }
}
