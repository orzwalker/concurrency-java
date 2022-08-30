package com.lock;

/**
 * 模拟转账
 *
 * @author walker
 * @since 2021/12/13 11:59
 */
public class DeadLock {

    /**
     * 余额
     */
    private int balance;

    /**
     * 如果相互转账，就会死锁
     */
    public void transfer(DeadLock target, int amt) {
        // 锁定转出账户
        synchronized (this) {
            // 锁定转入账户
            synchronized (target) { // A、B相互转账，并发时，改行都拿不到需要的资源，开始循环等待，造成死锁
                if (balance > amt) {
                    balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}
