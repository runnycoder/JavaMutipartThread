package com.icehan.thread.lock;

public class LeftRightDeadLock {
    private final Object left = new Object();
    private final Object right = new Object();

    public void leftRight(){
        synchronized(left){
            synchronized (right){
                System.out.println("left->right lock ");
            }
        }
    }

    public void rightLeft(){
        synchronized (right){
            synchronized (left){
                System.out.println("right->left lock ");
            }
        }
    }
}
