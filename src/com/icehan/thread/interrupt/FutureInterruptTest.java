package com.icehan.thread.interrupt;

import java.util.concurrent.*;

public class FutureInterruptTest {
    private static ExecutorService  taskExec = Executors.newCachedThreadPool();

    public static void timedRun(Runnable r, long timeout, TimeUnit unit){
        Future<?> task = taskExec.submit(r);
        try {
            task.get(timeout,unit);
        } catch (InterruptedException e) {
            System.out.println("task is interrupted");
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            System.out.println("task outtime");
            boolean interrupted= task.cancel(true);
            System.out.println("task interrupt "+interrupted);
            taskExec.shutdown();
        }
    }

//    public static void main(String[] args) {
//        Runnable r = new Runnable(){
//            @Override
//            public void run() {
//                while (true){
//                    System.out.println(Thread.currentThread().getName()+" is running!");
//                }
//            }
//        };
//        timedRun(r, 10, TimeUnit.SECONDS);
//    }


        public static void main(String[] args) {
        Runnable r = new Runnable(){
            @Override
            public void run() {
                try {
                    while (!Thread.currentThread().isInterrupted()){
                        System.out.println(Thread.currentThread().getName()+" is running!");
//                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    System.out.println("is interrupted?");
                }
            }
        };
        timedRun(r, 3, TimeUnit.SECONDS);

    }
}
