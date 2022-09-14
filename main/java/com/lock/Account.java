package com.lock;


import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 串行化转账
 */
public class Account {

    private float balance;


    /**
     * 并发时，会有问题
     * 没有任何锁，两个线程同时给A转入，结果可能不正确
     */
    void transfer(Account target, int num) {
        if (this.balance > num) {
            this.balance -= num;
            target.balance += num;
        }
    }

    /**
     * 看似是一个锁锁了两个资源，但实际上锁的是this这个对象，
     * 被转账的对象还没有被锁，会有并发问题
     * 比如A->B B->C后，B的余额可能会有问题 ==== 因为并发一开始A、B读到的B余额都是一样的
     * <p>
     * synchronized：非静态方法锁this当前对象；静态方法锁Object.class，Object指具体的class
     */
    synchronized void transferV1(Account target, int num) {
        if (this.balance > num) {
            this.balance -= num;
            target.balance += num;
        }
    }


    /**
     * 完全串行化 解决上边的问题
     * 一个锁锁两个资源，并且锁的是Account这个class，
     * 这样的话，单机下所有账户之间的交易完全串行化====现实中是不可能的，效率太低了
     */
    void transferV2(Account target, int num) {
        synchronized (Account.class) {
            if (this.balance > num) {
                this.balance -= num;
                target.balance += num;
            }
        }
    }

    // ============================
    Lock lock = new ReentrantLock();

    void transferV3(Account target, int num) {
        while (true) {
            // 非阻塞式获取锁，不会出现死锁，但是会活锁（非阻塞式死锁）
            if (this.lock.tryLock()) { // T1、T2线程相互转账，第一次获取自己的锁都成功了
                try {
                    if (target.lock.tryLock()) { // 获取对方锁时，失败，都释放了锁资源，while循环一直在尝试获取锁
                        try {
                            if (this.balance > num) {
                                this.balance -= num;
                                target.balance += num;
                            }
                        } finally {
                            target.lock.unlock();
                        }
                    }
                } finally {
                    this.lock.unlock();
                }
            }
        }

    }

    void transferV3Deal(Account target, int num) {
        int max = 100, min = 1;
        while (true) {
            // 非阻塞式获取锁，不会出现死锁，但是会活锁（非阻塞式死锁）
            if (this.lock.tryLock()) { // T1、T2线程相互转账，第一次获取自己的锁都成功了
                try {
                    // 获取第二把锁前随机休眠一段时间，防止活锁
                    int waitTime = ThreadLocalRandom.current().nextInt(min, max + 1);
                    TimeUnit.MILLISECONDS.sleep(waitTime);
                    if (target.lock.tryLock()) { // 获取对方锁时，失败，都释放了锁资源，while循环一直在尝试获取锁
                        try {
                            if (this.balance > num) {
                                this.balance -= num;
                                target.balance += num;
                            }
                        } finally {
                            target.lock.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                } finally {
                    this.lock.unlock();
                }
            }
        }

    }
}

