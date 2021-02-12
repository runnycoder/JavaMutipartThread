package com.icehan.thread.threadsafe.utils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.function.DoublePredicate;

public class ForkJoinTest {
    static class Counter extends RecursiveTask<Double>{
        public static final int THRESHOLD = 100;
        private double[] values;
        private int from;
        private int to;
        private DoublePredicate filter;

        public Counter(double[] values, int from, int to, DoublePredicate filter) {
            this.values = values;
            this.from = from;
            this.to = to;
            this.filter = filter;
        }

        @Override
        protected Double compute() {
            if(to-from<THRESHOLD){
                double count = 0;
                for (int i = from; i < to ; i++) {
                    if(filter.test(values[i])){
                        count+=values[i];
                    }
                }
                return count;
            }else{
                int mid = (from + to)/2;
                Counter first = new Counter(values, from, mid, filter);
                Counter second = new Counter(values, mid, to, filter);
                invokeAll(first,second);
                return first.join()+second.join();
            }
        }
    }

    public static void main(String[] args) {
        final int SIZE = 10000000;
        double[] numbers = new double[SIZE];
        double total = 0;
        for (int i = 0; i < SIZE; i++) {
            numbers[i] = Math.random();
            if(numbers[i]>0.5){
                total+=numbers[i];
            }
        }
        Counter counter = new Counter(numbers, 0, numbers.length, x -> x > 0.5);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(counter);
        System.out.println(counter.join());
        System.out.println(total);
    }
}
