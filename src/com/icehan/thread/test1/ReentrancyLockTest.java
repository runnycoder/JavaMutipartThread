package com.icehan.thread.test1;

public class ReentrancyLockTest {
    public static class Widget{
        public synchronized void doSth(){
            System.out.println("father dosth!");
        }
    }

    public static class LoggingWidget extends Widget{
        public synchronized void doSth(){
            System.out.println(toString()+": calling dosth");
            super.doSth();
        }
    }

    /**
     * 此处演示中如果synchronized关键字持有的锁对于线程是不可重入的
     * 那么对父类方法的调用永远也无法获取到锁
     * @param args
     */
    public static void main(String[] args) {
        LoggingWidget loggingWidget = new LoggingWidget();
        loggingWidget.doSth();
    }
}
