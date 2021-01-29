package com.icehan.thread.semaphore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class BoundedBuffer<E> {
    private final Semaphore availableItems,availableSpaces;
    private final E[] items;
    private int putPosition=0,takePosition=0;



    public BoundedBuffer(int capacity) {
        this.availableItems = new Semaphore(0);
        this.availableSpaces = new Semaphore(capacity);
        this.items = (E[])new Object[capacity];
    }

    public boolean isEmpty(){
        return availableItems.availablePermits()==0;
    }

    public  boolean isFull(){
        return availableSpaces.availablePermits()==0;

    }

    public synchronized void doInsert(E x){
        int i = putPosition;
        items[i] = x;
        putPosition=(++i==items.length)?0:i;
    }

    public void put(E x) throws InterruptedException {
        availableSpaces.acquire();
        doInsert(x);
        availableItems.release();
    }

    public synchronized E doExtract(){
        int i = takePosition;
        E x = items[i];
        items[i]=null;
        takePosition = (++i==items.length)?0 : i;
        return x;
    }

    public E take() throws InterruptedException {
        availableItems.acquire();
        E item = doExtract();
        availableSpaces.release();
        return item;
    }

    /**
     * 测试arrayList的动态扩容
     * @param args
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < 11 ; i++) {
            list.add("str"+i);
            System.out.println("current list size="+list.size());
            Class<? extends ArrayList> aClass = list.getClass();
            Field elementData = aClass.getDeclaredField("elementData");
            elementData.setAccessible(true);
            Object[] elementDataArray = (Object[])elementData.get(list);
            System.out.println("the arrayList real length"+elementDataArray.length);
        }
    }
}
