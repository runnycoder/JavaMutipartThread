package com.icehan.thread.semaphore;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SemaphoreTest {
    private static   Semaphore semaphore = new Semaphore(3);

    static class Student extends Thread{
        private Semaphore sp = null;
        private String name = null;

        public Student(Semaphore sp,String name) {
            this.sp = sp;
            this.name = name;
        }

        @Override
        public void run(){
            try {
                System.out.println(name+"进入了就绪队列");
                sp.acquire();
                System.out.println(name+"获取了许可");
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(name+"释放了许可");
                sp.release();
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i <10 ; i++) {
            new Student(semaphore, "学生"+i).start();

        }
    }
}
