package com.icehan.thread.sychronizer;

import java.util.Objects;

/**
 * 有限缓存的不同实现基类
 */
public class BaseBoundedBuffer<V> {
    private final V[] buf;
    private int tail;
    private int head;
    private int count;

    protected BaseBoundedBuffer(int capacity){
        this.buf = (V[])new Objects[capacity];
    }

    protected synchronized final void doPut(V v){
        buf[tail]=v;
        if(++tail==buf.length){
            tail=0;
        }
        count++;
    }

    protected synchronized final V doTake(){
        V v = buf[head];
        buf[head] = null;
        if(++head == buf.length){
            head = 0;
        }
        count--;
        return v;
    }

    public synchronized final boolean isFull(){
        return count == buf.length;
    }

    public synchronized final boolean isEmpty(){
        return count == 0;
    }

//    static int left,center,right;
//    public static void main(String[] args) {
////        int[] arr = {1,7,3,6,5,6};
////        System.out.println(pivotIndex(arr));
//
//        int[] arr = {1,3,5,6};
//        int index = searchInsert(arr, 2);
//        if(index==-1){
//            index=right+1;
//        }
//        System.out.println(index);
//    }
//
//
//    public static int searchInsert(int[] nums, int target) {
//        left=0;
//        right=nums.length-1;
//        int index = binarySearch(nums, target);
//        System.out.println("left="+left);
//        System.out.println("right"+right);
//        System.out.println("center"+center);
//        if(index==-1){
//            index = (center>left?center-1:center+1);
//        }
//        return index;
//    }
//
//    public static int binarySearch(int[] nums,int target){
//        if(left>right){
//            return -1;
//        }
//        center = (left+right)/2;
//        if(nums[center]==target){
//            return center;
//        }else if(target<nums[center]){
//            right = center-1;
//        }else{
//            left  = center+1;
//        }
//        return binarySearch(nums,target);
//    }
//


}
