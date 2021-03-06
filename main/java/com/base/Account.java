package com.base;

/**
 * 模拟转账
 *
 * @author walker
 * @since 2021/12/13 11:59
 */
public class Account {

    /**
     * 余额
     */
    private int balance;

    /**
     * 转账
     * 如果相互转账，就会死锁
     *
     * @param target 目标账户
     * @param amt    转账金额
     */
    public void transfer(Account target, int amt) {
        // 锁定转出账户
        synchronized (this) {
            // 锁定转入账户
            synchronized (target) {
                if (balance > amt) {
                    balance -= amt;
                    target.balance += amt;
                }
            }
        }
    }
}
