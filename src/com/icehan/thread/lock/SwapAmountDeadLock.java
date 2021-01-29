package com.icehan.thread.lock;

public class SwapAmountDeadLock {
    static class Account{
        private Integer balance;

        public Integer getBalance() {
            return balance;
        }

        public void setBalance(Integer balance) {
            this.balance = balance;
        }
    }

    /**
     * 这种方式可能会存在死锁 因为传入参数的顺序可能就决定了加锁的顺序
     * 假设A向B转账的同时 B向A进行转账 就有可能互相持有对方所有的锁进入死锁
     * @param fromAccount
     * @param toAccount
     * @param amount
     */
    public void transferMoney(Account fromAccount,Account toAccount,Integer amount){
        synchronized (fromAccount){
            synchronized (toAccount){
                if(fromAccount.getBalance()>amount){
                    fromAccount.setBalance(fromAccount.getBalance()-amount);
                    toAccount.setBalance(fromAccount.getBalance()+amount);
                }
            }
        }
    }

    private static final Object tieLock = new Object();

    /**
     * 解决的方式就是控制加锁的顺序 通过计算hash值的方式控制加锁顺序
     * 假设hash值相同的话 需要添加一个额外的锁保证一次只有一个线程操作这个有风险的加锁方式
     * 从而减小发生的可能性
     * 如果Account有一个唯一的账号标识的话(比如账号)就更好控制加锁的顺序了 可以使用账号进行排序决定加锁的顺序避免死锁的发生
     * @param fromAccount
     * @param toAccount
     * @param amount
     */
    public void safeTransferMoney(Account fromAccount,Account toAccount,Integer amount){
        class Helper{
            public void transfer(){
                if(fromAccount.getBalance()>amount){
                    fromAccount.setBalance(fromAccount.getBalance()-amount);
                    toAccount.setBalance(toAccount.getBalance()+amount);
                }
            }
        }

        int fromHash = System.identityHashCode(fromAccount);
        int toHash = System.identityHashCode(toAccount);
        if(fromHash<toHash){
            synchronized (fromAccount){
                synchronized(toAccount){
                    new Helper().transfer();
                }
            }
        }else if(fromHash>toHash){
            synchronized (toAccount){
                synchronized (fromAccount){
                    new Helper().transfer();
                }
            }
        }else{
            synchronized (tieLock){
                synchronized (fromAccount){
                    synchronized(toAccount){
                        new Helper().transfer();
                    }
                }
            }
        }
    }

}
