package com._thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

/**
 * 测试线程生命周期状态
 * BLOCKED是被动的，线程在同步队列中
 * WAITING是主动的，线程在条件等待队列中，被唤醒后进入同步队列
 */
public class ThreadStateTest {

    public static void main(String[] args) {
        // testBlockedState();
        // testWaitingStateWithWait();
        // testWaitingStateWithPark();
        testTimedWaitingStateWithSleep();
    }

    /**
     * 测试{@link Thread.State#BLOCKED}
     * 只有竞争隐式锁synchronized失败后，进入了同步等待队列准备下次竞争时，才会到达blocked状态
     */
    static void testBlockedState() {
        Object lock = new Object();
        Thread a = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程a拿到了synchronized锁");
                System.out.println("执行业务逻辑A");
                _sleep(100);
            }
        }, "a");
        Thread b = new Thread(() -> {
            synchronized (lock) {
                System.out.println("线程b拿到了synchronized锁");
                System.out.println("执行业务逻辑B");
                _sleep(100);
            }
        }, "b");

        a.start();
        b.start();
        System.out.println("====>OK");

        /*
         * 假设a线程先拿到锁
         *
         * 线程a拿到了synchronized锁
         * 执行业务逻辑A
         *
         * 名称: a
         * 状态: TIMED_WAITING
         * 总阻止数: 0, 总等待数: 1
         *
         * 名称: b
         * 状态: java.lang.Object@2394465b上的BLOCKED, 拥有者: a
         * 总阻止数: 1, 总等待数: 0
         */
    }

    /**
     * 测试{@link Thread.State#WAITING}
     */
    static void testWaitingStateWithWait() {
        Object lock = new Object();
        AtomicInteger num = new AtomicInteger(0);
        Thread a = new Thread(() -> {
            synchronized (lock) {
                while (num.get() < 1) {
                    System.out.println("v1 num=" + num.get());
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("v2 num=" + num.get());
            }
        }, "a");

        // 启动a线程，进入条件等待队列19秒，只输出v1
        a.start();
        _sleep(20);

        // 主线程修改while条件变量
        num.incrementAndGet();

        // 唤醒a线程，输出v2
        synchronized (lock) {
            lock.notifyAll();
        }
        _sleep(100);

        /*
         * v1 num=0
         * v2 num=1
         *
         * 名称: a
         * 状态: java.lang.Object@62f378fc上的WAITING
         * 总阻止数: 0, 总等待数: 1
         *
         * 名称: main
         * 状态: TIMED_WAITING
         * 总阻止数: 0, 总等待数: 2
         */
    }


    /**
     * 测试{@link Thread.State#WAITING}
     */
    static void testWaitingStateWithPark() {
        Thread a = new Thread(() -> {
            System.out.println("线程a执行");
            LockSupport.park();
        }, "a");

        // a线程启动后一直处于阻塞状态
        a.start();
        _sleep(20);
        // 唤醒a线程
        LockSupport.unpark(a);
        _sleep(100);

        /*
         * 线程a执行
         *
         * 名称: a
         * 状态: WAITING
         * 总阻止数: 0, 总等待数: 1
         */
    }

    /**
     * 测试{@link Thread.State#TIMED_WAITING}
     */
    static void testTimedWaitingStateWithSleep() {
        new Thread(() -> {
            System.out.println("线程a执行");
            _sleep(20);
        }, "a").start();
        _sleep(100);

        /*
         * 名称: a
         * 状态: TIMED_WAITING
         * 总阻止数: 0, 总等待数: 1
         */
    }

    static void _sleep(int time) {
        if (time > 0) {
            try {
                Thread.sleep(time * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
