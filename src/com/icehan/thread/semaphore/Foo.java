package com.icehan.thread.semaphore;

public class Foo {

    private final Thread thread;

    public Foo() {
        System.out.println("foo init");
        thread = new Thread(new Bar(), "F");
        thread.start();
    }

    public void run() {
        synchronized (this) {
            System.out.println("main run im here");
            thread.interrupt();
            try {
                System.out.println("main thread join before");
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Foo run method");
        }
    }

    private final class Bar implements Runnable {

        @Override
        public void run() {
            System.out.println("bar in run");
            synchronized (Foo.this) {
                System.out.println("Bar run method");
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        final Foo foo = new Foo();
        foo.run();
    }

}
