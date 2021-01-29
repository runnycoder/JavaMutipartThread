package com.icehan.thread.test1;

public class ThreadLocalTest {
    static ThreadLocal<String> localVar = new ThreadLocal<>();
    static ThreadLocal<String> localVar2 = new ThreadLocal<>();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable(){

            @Override
            public void run() {
                localVar.set(""+Thread.currentThread());
                localVar2.set(Thread.currentThread()+" second var");
                System.out.println(localVar.get());
                System.out.println(localVar2.get());
            }
        });

        Thread t2 = new Thread(new Runnable(){

            @Override
            public void run() {
                localVar.set(""+Thread.currentThread());
                localVar2.set(Thread.currentThread()+" second var");
                System.out.println(localVar.get());
                System.out.println(localVar2.get());
            }
        });
        t1.start();
        t2.start();
        Thread.sleep(1000);

        System.out.println(localVar.get());

    }
}
