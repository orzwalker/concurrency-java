package com.lock;

import java.util.ArrayList;
import java.util.List;

/**
 * @author walker
 * @since 2021/12/13 14:18
 */
public class Account4DeadLock {

    private int balance;

    static volatile Allocator allocator;

    // 操作临界区资源时，先尝试一次性获取所有资源
    public void transfer(Account4DeadLock target, int amt) {
        allocator = getInstance();

        if (allocator.reply(this, target)) {
            // 一次性获取到资源
            try {
                // 加锁后，保障this.balance和target.balance在其他不同线程的可见性
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

    /**
     * 获取单例资源
     * 双重检测获取单例对象
     */
    static Allocator getInstance() {
        if (allocator == null) {
            // 全局唯一资源 Allocator.class，对所有Allocator共享
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
 */
class Allocator {
    private List<Object> list = new ArrayList<>();

    /**
     * 一次性申请所有资源
     */
    synchronized public boolean reply(Object from, Object to) {
        while (list.contains(from) || list.contains(to)) {
            // 条件不满足时，加入到等待队列
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        list.add(from);
        list.add(to);
        return true;
    }

    /**
     * 释放资源
     */
    synchronized public boolean free(Object from, Object to) {
        if (list.size() > 0) {
            list.remove(from);
            list.remove(to);

            // 释放互斥锁后，唤醒等待队列
            notifyAll();
            return true;
        }
        return false;
    }
}
