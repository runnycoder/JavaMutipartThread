package com.icehan.thread.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 测试ConcurrentHashMap的外部加锁
 * 使用map对象本身做外部锁 并不能完成CAS操作的原子性 说明在ConcurrentHashMap的内部使用的锁并不是对象本身
 */
public class ConcurrentHashMapTest {
    private static ConcurrentHashMap<Integer,Integer> map = new ConcurrentHashMap<>(16);

    public static void main(String[] args) {
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                synchronized (map){
                    for (int i = 0; i < 10; i++) {
                        System.out.println("Thread1--"+i);
                        if(!map.contains(i)){
                            System.out.println("map:"+map.put(i,i)+"  i:"+i);
                        }
                    }
                }
            }
        });

        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run() {
                    for (int i = 0; i < 10; i++) {
                        System.out.println("Thread2--"+i);
                        map.put(i, 99);
                    }
            }
        });

        t1.start();
        t2.start();
    }
}
