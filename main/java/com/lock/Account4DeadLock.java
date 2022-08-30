package com.lock;

import java.util.ArrayList;
import java.util.List;

/**
 * 死锁的条件
 * 1、互斥
 * 2、占住资源不放
 * 3、不可抢占
 * 4、循环等待
 * <p>
 * 因此只要破坏其中的一个条件，死锁就被解开了
 * 但是「互斥」不能破坏————加锁的目的就是为了互斥
 * 其他三个条件可以破坏
 * 2、一次性拿齐所需要的资源————一个个拿才出现的等待所需要的资源被释放
 * 3、设置超时时间，等待超时后主动释放自己占有的资源
 * 4、顺序申请所需资源————资源有线性顺序
 *
 * @author walker
 * @since 2021/12/13 14:18
 */
public class Account4DeadLock {

    private int balance;
    /**
     * 对象id，有序性
     */
    private int id;

    static volatile Allocator allocator;

    // 操作临界区资源时，先尝试一次性获取所有资源
    public void transfer(Account4DeadLock target, int amt) {
        allocator = getInstance();

        if (allocator.reply(this, target)) {
            // 一次性获取到资源
            try {
                // 加锁后，保障this.balance和target.balance在其他不同线程的可见性
                // 解锁 Before Happens 加锁 + 传递性
                synchronized (this) {
                    synchronized (target) {
                        if (balance > amt) {
                            balance -= amt;
                            target.balance += amt;
                        }
                    }
                }
            } finally {
                allocator.free(this, target);
            }
        }
    }

    // 获取临界资源时，顺序获取————解决循环等待导致的死锁问题
    public void transferV2(Account4DeadLock target, int amt) {
        Account4DeadLock first = this;
        Account4DeadLock second = target;

        if (this.id > target.id) {
            first = target;
            second = this;

            // 锁定序号小的账户
            synchronized (first) {
                // 锁定序号大的账户
                synchronized (second) {
                    if (this.balance > amt) {
                        this.balance -= amt;
                        target.balance += amt;
                    }
                }
            }
        }

    }


    /**
     * 获取单例资源
     * 双重检测获取单例对象
     */
    static Allocator getInstance() {
        if (allocator == null) {
            // 全局唯一资源 Allocator.class，对所有Allocator共享
            // 实际上是单机版
            synchronized (Allocator.class) {
                if (null == allocator) {
                    allocator = new Allocator();
                }
            }
        }
        return allocator;
    }

}

/**
 * 一次性获取到需要的全部资源
 * 和synchronized(Account.class)的区别：
 * 1、synchronized(Account.class)锁住了账户这个对象，单机下所有的转账只能串行，比如A转B、C转D
 * 2、使用Allocator，因为list中存放的是转账用户的账户，所以拿锁是串行的，但是不同账户之间转账可以是并行的，比如A转B、C转D
 */
class Allocator {

    /**
     * 临界区资源，非空说明有线程占用共享资源
     */
    private final List<Object> list = new ArrayList<>();

    /**
     * 一次性申请所有资源
     * 问题：锁住了所有账户，成本太高了，不适用
     */
    synchronized public boolean reply(Object from, Object to) {
        while (list.contains(from) || list.contains(to)) {
            // 其他线程占用了资源，需要等待
            try {
                // 进入等待队列
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }

        // 其他线程释放了共享资源，当前线程可以占用了
        list.add(from);
        list.add(to);
        return true;
    }

    /**
     * 释放资源
     */
    synchronized public void free(Object from, Object to) {
        if (list.size() > 0) {
            list.remove(from);
            list.remove(to);

            // 释放互斥锁后，唤醒等待队列
            notifyAll();
        }
    }
}
