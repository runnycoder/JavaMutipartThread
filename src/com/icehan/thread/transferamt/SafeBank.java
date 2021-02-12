package com.icehan.thread.transferamt;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SafeBank {
    private final double[] accounts;
    private Lock lock;
    private Condition sufficientFunds;

    public SafeBank(int n, double initialBlance) {
        this.accounts = new double[n];
        Arrays.fill(accounts, initialBlance);
        lock = new ReentrantLock();
        sufficientFunds = lock.newCondition();
    }

    public void transfer(int from, int to, double amount) throws InterruptedException {
        lock.lock();
        try {
//            if(accounts[from]<amount){
//                return;
//            }
            while (accounts[from] < amount) {
                sufficientFunds.await();
            }
            System.out.println(Thread.currentThread());
            accounts[from] -= amount;
            System.out.printf(System.currentTimeMillis() + "  %10.2f from %d to %d ", amount, from, to);
            accounts[to] += amount;
            System.out.printf("totalBalance: %10.2f%n", getTotalBalance());
            //转账操作完成后唤醒其它所有等待线程 尝试条件判断
            sufficientFunds.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public double getTotalBalance() {
        lock.lock();
        try {
            double sum = 0;
            for (double amt :
                    accounts) {
                sum += amt;
            }
            return sum;
        } finally {
            lock.unlock();
        }

    }

    public int size() {
        return accounts.length;
    }

    public static void main(String[] args) throws InterruptedException {
        int n = 1000;
        double max_amt = 1000;
        SafeBank bank = new SafeBank(n, 1000);
        /**
         * 这种方式加锁和条件等待唤醒都没有问题但是由于
         * 等待账户余额>转账金额的条件比较男满足所以很快就会所有的线程都在condition上等待
         */

        for (int i = 0; i < n; i++) {
            int fromAccount = i;
            new Thread(() -> {
                try {
                    while (true) {
                        int toAccount = (int) (bank.size() * Math.random());
                        double amt = 1000 * Math.random();
                        bank.transfer(fromAccount, toAccount, amt);
                        Thread.sleep((int) (10 * Math.random()));
                    }
                } catch (Exception e) {

                }
            }).start();
        }
    }

}
