package com.icehan.thread.test1;

import java.util.concurrent.*;

public class FutureTaskTest {
    private final ConcurrentHashMap<Object, Future<String>> taskCache = new ConcurrentHashMap<>();
    private String executionTask(final String taskName) throws Exception{
        while (true){
            Future<String> future = taskCache.get(taskName);
            if(future==null){
                Callable<String>  task= () -> taskName;
                FutureTask<String> futureTask = new FutureTask<>(task);
                future = taskCache.putIfAbsent(taskName, futureTask);//不存在添加任务返回null 存在返回原有值
                if(future==null){
                    future = futureTask;
                    futureTask.run();
                }
            }
            try {
                return future.get();
            } catch (Exception e) {
                taskCache.remove(taskName, future);
            }
        }
    }
}
