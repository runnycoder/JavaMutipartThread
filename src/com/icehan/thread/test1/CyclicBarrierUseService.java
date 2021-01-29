package com.icehan.thread.test1;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 多线程关卡阻塞测试
 */
public class CyclicBarrierUseService implements  Runnable{

    private CyclicBarrier cyclicBarrier = new CyclicBarrier(4,this);

    private Executor executor = Executors.newFixedThreadPool(4);

    private ConcurrentMap<String,Integer> count = new ConcurrentHashMap<>();

    public static String mapToString(ConcurrentMap<String, Integer> map) {
        if(map == null) {
            return "{}";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("{ ");
        for(String s : map.keySet()) {
            sb.append("\""+s+"\":\""+map.get(s)+"\",");
        }
        sb.replace(sb.length()-1, sb.length(), "}");
        return sb.toString();
    }


    @Override
    public void run() {
        int result = 0;
        for (Map.Entry<String,Integer> entry:
             count.entrySet()) {
            result+= entry.getValue();
        }
        count.put(Thread.currentThread().getName()+"_result", result);
        System.out.println(mapToString(count));
    }

    public void calculate(){
        for (int i = 0; i <4 ; i++) {
//            executor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    count.put(Thread.currentThread().getName(), 10);
//                    try {
//                        System.out.println(Thread.currentThread().getName()+"is waiting");
//                        cyclicBarrier.await();
//                        System.out.println(Thread.currentThread().getName()+"is working");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    } catch (BrokenBarrierException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    count.put(Thread.currentThread().getName(), 10);
                    try {
                        System.out.println(Thread.currentThread().getName()+"is waiting");
                        cyclicBarrier.await();
                        System.out.println(Thread.currentThread().getName()+"is working");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CyclicBarrierUseService cyclicBarrierUseService = new CyclicBarrierUseService();
        cyclicBarrierUseService.calculate();
//        Thread.sleep(10000);
//        System.out.println("main waiting 10s reset cyclicBarrier");
//        cyclicBarrierUseService.cyclicBarrier.reset();

    }

}
