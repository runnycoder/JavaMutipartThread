package com.icehan.thread.cas;

import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用CAS避免多元的不变约束
 */
public class CasNumberRange {

    private static class IntPair{
        final int lower;
        final int upper;

        public IntPair(int lower, int upper) {
            this.lower = lower;
            this.upper = upper;
        }
    }

    private final AtomicReference<IntPair> values = new AtomicReference<>(new IntPair(0, 0));

    public int geLower(){return values.get().lower;}
    public int getUpper(){return values.get().upper;}

    public void setLower(int i ){
        while (true){
            IntPair oldV = values.get();
            if(i>oldV.upper){
                System.out.println("can't set lower to "+i+">upper");
                break;
            }
            //判断旧值(lower是否发生了变动)如果未被修改 则设置新值
            IntPair newV = new IntPair(i, oldV.upper);
            if (values.compareAndSet(oldV, newV)) {
                return;
            }
        }
    }

    public void setUpper(int i){
        while (true){
            IntPair oldV = values.get();
            if(i<oldV.lower){
                System.out.println("can't set upper to "+i+"<lower");
                break;
            }
            IntPair newV = new IntPair(oldV.lower, i);
            //判断旧值(lower是否发生了变动)如果未被修改 则设置新值
            if(values.compareAndSet(oldV, newV)){
                return;
            }
        }
    }
}
