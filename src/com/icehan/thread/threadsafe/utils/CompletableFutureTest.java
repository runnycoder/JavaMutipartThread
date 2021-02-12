package com.icehan.thread.threadsafe.utils;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class CompletableFutureTest {
    @Test
    public void test(){
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(()->{
            int i = 1/0;
            return 100;
        });
        try {
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
//        future.join();
    }

    public static CompletableFuture<Integer> compute() {
        final CompletableFuture<Integer> future = new CompletableFuture<>();
        return future;
    }
    @Test
    public void test1() throws Exception {
        final CompletableFuture<Integer> f = compute();
        class Client extends Thread {
            CompletableFuture<Integer> f;
            Client(String threadName, CompletableFuture<Integer> f) {
                super(threadName);
                this.f = f;
            }
            @Override
            public void run() {
                try {
                    System.out.println(this.getName() + ": " + f.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        Client client1 = new Client("Client1", f);
        Client client2 = new Client("Client2", f);
        client1.start();
        client2.start();
        Thread.sleep(1000);

        //新建一个线程五秒之后解除线程1 2 的阻塞状态
        new Thread(()->{
            try {
                Thread.sleep(5000);
                System.out.println("I save the world!");
                //f.complete(100); //可以解除get的阻塞
                f.completeExceptionally(new Exception()); //可以解除get的阻塞
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.out.println("waiting");
        client1.join();
        client2.join();
        System.out.println("runover");
    }


    private static Random rand = new Random();
    private static long t = System.currentTimeMillis();
    static int getMoreData() {
        System.out.println("begin to start compute");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("end to start compute. passed " + (System.currentTimeMillis() - t)/1000 + " seconds");
        return rand.nextInt(1000);
    }
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(CompletableFutureTest::getMoreData);
        CompletableFuture<Integer> f = integerCompletableFuture.whenComplete((v, e) -> {
            System.out.println(v);//第一个Future返回的结果
            System.out.println(e);//第一个Future返回的异常
        });
        System.out.println(f.get());//方法正常执行获取的原始任务的执行结果
    }

    @Test
    public void test3() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> uCompletableFuture = CompletableFuture.supplyAsync(() -> {
            int i = 0;
            boolean flag = false;
            if(flag){
                i = 1 / 0;
            }
            return i;
        });
        //如果uCompletableFuture执行异常走exceptionally如果正常走thenAccept
        CompletableFuture<Integer> exceptionally = uCompletableFuture.exceptionally((e) -> {
            System.out.println("its exceptionally");
            e.printStackTrace();
            return -1;
        });
        CompletableFuture<Void> voidCompletableFuture = uCompletableFuture.thenAccept((param) -> {
            System.out.println("param=" + param);
        });
        voidCompletableFuture.get();
    }

    @Test
    public void test4() throws ExecutionException, InterruptedException {
        //创建异步任务
        CompletableFuture<Double> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread()+"job1 start ,time="+System.currentTimeMillis());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(false){
                throw new RuntimeException("job1 test");
            }else{
                return 1.2;
            }
        });

        CompletableFuture<Object> f2 = f1.handle((v, e) -> {
            System.out.println(Thread.currentThread()+"job2 start ,time="+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            if(e!=null){
                System.out.println("error stack trace");
                e.printStackTrace();
            }else{
                System.out.println("f1 run success,result is "+v);
            }
            if(e!=null){
                return "run error";
            }else{
                return "run over";
            }
        });
        System.out.println("main thread waiting subThread "+System.currentTimeMillis());
        //get返回的是f1回调函数的返回值 跟f1已经没有关系了
        System.out.println("run result "+f2.get());
        System.out.println("main run over "+System.currentTimeMillis());
    }

    @Test
    public void test5() throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        //创建异步任务
        CompletableFuture<Double> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
            return 1.2;
        });

        CompletableFuture<Double> f2 = CompletableFuture.supplyAsync(() -> {
            System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
            return 3.2;
        });

        CompletableFuture<Object> f3 = f1.thenCombine(f2, (v1, v2) -> {
            System.out.println(Thread.currentThread()+" start job3,time->"+System.currentTimeMillis());
            System.out.println("job3 param v1->"+v1+",v2->"+v2);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job3,time->"+System.currentTimeMillis());
            return v1+v2;
        });
        System.out.println("f3 result = "+f3.get());

        //有f1,f2返回值作为参数 但没有返回值
        CompletableFuture<Void> f4 = f1.thenAcceptBoth(f2, (v1, v2) -> {
            System.out.println(Thread.currentThread()+" start job4,time->"+System.currentTimeMillis());
            System.out.println("job4 param v1->"+v1+",v2->"+v2);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job4,time->"+System.currentTimeMillis());
        });
        System.out.println("f4 result = "+f4.get());

        CompletableFuture<Void> f5 = f4.runAfterBoth(f3, () -> {
            System.out.println(Thread.currentThread() + " start job5,time->" + System.currentTimeMillis());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("cf5 do something");
            System.out.println(Thread.currentThread() + " exit job5,time->" + System.currentTimeMillis());
        });
        System.out.println("f5 result = "+f5.get());
    }

    /**
     * applyToEither / acceptEither / runAfterEither
     *      这三个方法都是将两个CompletableFuture组合起来，只要其中一个执行完了就会执行某个任务，
     *      其区别在于applyToEither会将已经执行完成的任务的执行结果作为方法入参，并有返回值；
     *      acceptEither同样将已经执行完成的任务的执行结果作为方法入参，但是没有返回值；
     *      runAfterEither没有方法入参，也没有返回值。注意两个任务中只要有一个执行异常，则将该异常信息作为指定任务的执行结果。测试用例如下：
     */
    @Test
    public void test6() throws ExecutionException, InterruptedException {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
            return 1.2;
        });
        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
            return 3.2;
        });
        //cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf3,且有返回值
        CompletableFuture<Double> cf3=cf.applyToEither(cf2,(result)->{
            System.out.println(Thread.currentThread()+" start job3,time->"+System.currentTimeMillis());
            System.out.println("job3 param result->"+result);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job3,time->"+System.currentTimeMillis());
            return result;
        });

        //cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf3,无返回值
        CompletableFuture cf4=cf.acceptEither(cf2,(result)->{
            System.out.println(Thread.currentThread()+" start job4,time->"+System.currentTimeMillis());
            System.out.println("job4 param result->"+result);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job4,time->"+System.currentTimeMillis());
        });

        //cf4和cf3都执行完成后，执行cf5，无入参，无返回值
        CompletableFuture cf5=cf4.runAfterEither(cf3,()->{
            System.out.println(Thread.currentThread()+" start job5,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("cf5 do something");
            System.out.println(Thread.currentThread()+" exit job5,time->"+System.currentTimeMillis());
        });

        System.out.println("main thread start cf.get(),time->"+System.currentTimeMillis());
        //等待子任务执行完成
        System.out.println("cf run result->"+cf.get());
        System.out.println("main thread start cf5.get(),time->"+System.currentTimeMillis());
        System.out.println("cf5 run result->"+cf5.get());
        System.out.println("main thread exit,time->"+System.currentTimeMillis());
    }

    /**
     *  thenCompose方法会在某个任务执行完成后，将该任务的执行结果作为方法入参然后执行指定的方法，
     *  该方法会返回一个新的CompletableFuture实例，如果该CompletableFuture实例的result不为null，
     *  则返回一个基于该result的新的CompletableFuture实例；
     *  如果该CompletableFuture实例为null，则，然后执行这个新任务，测试用例如下：
     */
    @Test
    public void test7() throws ExecutionException, InterruptedException {
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
            return null;
        });
        CompletableFuture<String> cf2= cf.thenCompose((param)->{
            System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
            return CompletableFuture.supplyAsync(()->{
                System.out.println(Thread.currentThread()+" start job3,time->"+System.currentTimeMillis());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                System.out.println(Thread.currentThread()+" exit job3,time->"+System.currentTimeMillis());
                return "job3 test";
//                return null;
            });
        });
        System.out.println("main thread start cf.get(),time->"+System.currentTimeMillis());
        //等待子任务执行完成
        System.out.println("cf run result->"+cf.get());
        System.out.println("main thread start cf2.get(),time->"+System.currentTimeMillis());
        System.out.println("cf2 run result->"+cf2.get());
        System.out.println("main thread exit,time->"+System.currentTimeMillis());

    }
}
