package com.icehan.thread.sychronizer.exception;

public class ThreadGate {
    /**
     * 条件 opened-since(n) (isOpen||generation>n)
     */
    private boolean isOpen;
    private int generation;

    public  synchronized void close(){
        isOpen = false;
    }

    public synchronized void open(){
        System.out.println(Thread.currentThread().getName()+" open method call!");
        ++generation;
        isOpen=true;
        notifyAll();
    }

    //阻塞 直到: opened-since(generation on entry)
    public synchronized void await() throws InterruptedException {
        int arrivalGeneration = generation;
        while (!isOpen||arrivalGeneration==generation){
            System.out.println(Thread.currentThread().getName()+" is in waiting!");
            wait();
            System.out.println(Thread.currentThread().getName()+" by notify!");
        }

    }

    public static void main(String[] args) {
        ThreadGate threadGate = new ThreadGate();
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+" is in running!");
                    threadGate.await();
                    System.out.println(Thread.currentThread().getName()+" is in run over!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName()+" is in running!");
                    threadGate.await();
                    System.out.println(Thread.currentThread().getName()+" is in run over!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        threadGate.open();
    }
}
