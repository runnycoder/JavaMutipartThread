package com.icehan.thread.test1;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierDemo {
    final int N;
    final float[][] data;
    final CyclicBarrier barrier;

    boolean done = true;

    int time =5;

    class  Worker implements Runnable{
        int myRow;
        Worker(int row){
            myRow = row;
        }
        @Override
        public void run() {
            while (done){
                for (float fs:
                     data[myRow]) {
                    System.out.println(Thread.currentThread().getName()+":"+fs);
                }
                try {
                    barrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public CyclicBarrierDemo(float[][] matrix){
        data = matrix;
        N = matrix.length;
        barrier = new CyclicBarrier(N, ()->{
            if(time--!=0){
                System.out.println("ok");
            }else {
                done = false;
            }
        });
        for (int i = 0; i < N; i++) {
            new Thread(new Worker(i)).start();
        }
    }

    public static void main(String[] args) {
        float[][] data = {
                {1f,2f},{3f,4f}
        };
        new CyclicBarrierDemo(data);
    }

}
