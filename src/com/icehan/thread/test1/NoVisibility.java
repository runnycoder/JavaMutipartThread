package com.icehan.thread.test1;

public class NoVisibility {
    private static boolean ready;
    private static int number;

    private static class ReaderThread extends Thread{
        public void run(){
            while (!ready){
                System.out.println(Thread.currentThread()+"is yield");
                Thread.yield();
            }

            System.out.println(number);
        }
    }
    /**主线程对于number和ready的赋值时间是不确定的
     *  子线程可能一直循环 最后输出42
     *  也可能输出0 (此处涉及到指令的重排序) ready的赋值先于number的赋值
     */

    public static void main(String[] args) throws InterruptedException {
        new ReaderThread().start();
        Thread.sleep(10000);
        number=42;
        ready=true;
    }
}
