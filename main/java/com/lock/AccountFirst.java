package com.lock;


/**
 * 串行化转账
 */
public class AccountFirst {

    private float balance;


    /**
     * 单机并发时，会有问题
     * 比如A->B B->C后，B的余额可能会有问题 ==== 因为并发一开始A、B读到的B余额都是一样的
     */
    public void transfer(AccountFirst target, int num) {
        if (this.balance > num) {
            this.balance -= num;
            target.balance += num;
        }
    }

    /**
     * 完全串行化 解决上边的问题
     * 单机时
     */
    public void transferV2(AccountFirst target, int num) {
        synchronized (AccountFirst.class) {
            if (this.balance > num) {
                this.balance -= num;
                target.balance += num;
            }
        }
    }
}
