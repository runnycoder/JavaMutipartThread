package com.icehan.thread.transferamt;

import java.util.Arrays;

public class Bank {
    private final double[] accounts;

    public Bank(int n,double initialBlance) {
        this.accounts = new double[n];
        Arrays.fill(accounts, initialBlance);
    }

    public void transfer(int from,int to,double amount) throws InterruptedException {
        if(accounts[from]<amount){
            return;
        }
        System.out.println(Thread.currentThread());
        accounts[from] -=amount;
        System.out.printf("%10.2f from %d to %d ",amount,from,to);
        accounts[to]+= amount;
        System.out.printf("totalBalance: %10.2f%n",getTotalBalance());
    }

    public double getTotalBalance(){
        double sum = 0;
        for (double amt:
             accounts) {
            sum+=amt;
        }
        return sum;
    }

    public int size(){
        return accounts.length;
    }

    public static void main(String[] args) {
        int n =1000;
        double max_amt = 1000;
        Bank bank = new Bank(n, 1000);
        for (int i = 0; i < n ; i++) {
            int fromAccount = i;
            new Thread(()->{
                try {
                    while (true){
                       int toAccount = (int)(bank.size()*Math.random());
                       double amt = 1000*Math.random();
                       bank.transfer(fromAccount, toAccount, amt);
                       Thread.sleep((int)(10*Math.random()));
                    }
                }catch (Exception e){

                }
            }).start();
        }
    }
}
