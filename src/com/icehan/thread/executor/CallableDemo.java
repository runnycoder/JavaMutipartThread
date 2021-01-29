package com.icehan.thread.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class CallableDemo {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < 10 ; i++) {
            Future<String> submit = executorService.submit(new TaskWithResult(i));
            futures.add(submit);
        }
        for (Future<String> future:
             futures) {
            while (!future.isDone());
            try {
                System.out.println(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        }
    }
}

class TaskWithResult implements Callable<String>{
    private int id;

    public TaskWithResult(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        System.out.println("call() 被自动调用!!!  "+Thread.currentThread().getName());
        return "call() 方法被自动调用，任务返回结果是:"+id+"  "+Thread.currentThread().getName();
    }
}
