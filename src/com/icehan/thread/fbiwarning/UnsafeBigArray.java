package com.icehan.thread.fbiwarning;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * java内置数组的最大大小为Integer.MAX_VALUE 使用直接内存分配可以不受JVM限制的分配内存
 * 但是必须注意要手动释放
 */
public class UnsafeBigArray {
    private  static  Unsafe unsafe = null;
    private  static  Field theUnsafe = null;

    static {
        try {
            theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    private static final int BYTE=1;
    private long size;
    private long address;//记录分配内存的地址

    public UnsafeBigArray(long size) {
        this.size = size;
        address = unsafe.allocateMemory(size*BYTE);
    }

    public void set(long i,byte value){
        unsafe.putByte(address+i*BYTE, value);
    }

    public int get(long index){
        return unsafe.getByte(address+index*BYTE);
    }

    public void clear(){
        unsafe.freeMemory(address);
    }

    public long size(){
        return size;
    }

    public static void main(String[] args) {

        System.out.println(Integer.toBinaryString(Integer.MAX_VALUE));
        System.out.println(Integer.toHexString(Integer.MAX_VALUE));

        UnsafeBigArray unsafeBigArray = new UnsafeBigArray((long)Integer.MAX_VALUE * 2);
        System.out.println("Array size:" + unsafeBigArray.size()); // 4294967294
        int sum=0;
        for (int i = 0; i < 100; i++) {
            unsafeBigArray.set((long)Integer.MAX_VALUE + i, (byte)3);
            sum += unsafeBigArray.get((long)Integer.MAX_VALUE + i);
        }
        System.out.println(sum);
        unsafeBigArray.clear();


    }
}
