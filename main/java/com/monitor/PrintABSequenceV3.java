package com.monitor;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 双线程顺序打印A、B
 * 使用ReentrantLock，支持公平、非公平锁
 */
public class PrintABSequenceV3 {

    /**
     * 保证原子性
     */
    AtomicBoolean flag = new AtomicBoolean(false);

    Lock lock = new ReentrantLock(true);

    /**
     * 等待打印A的条件等待队列
     */
    Condition wait_print_a_queue = lock.newCondition();
    /**
     * 等待打印B的条件等待队列
     */
    Condition wait_print_b_queue = lock.newCondition();


    /**
     * false 输出A
     */
    public void printA() {
        lock.lock();
        try {
            while (flag.get()) {
                // 打印A的条件不满足，进入等待条件队列，等待被B唤起
                wait_print_a_queue.await();
            }
            System.out.println(Thread.currentThread().getName() + "_A");
            flag.set(!flag.get());

            // 通知打印B
            wait_print_b_queue.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public void printB() {
        lock.lock();
        try {
            while (!flag.get()) {
                wait_print_b_queue.await();
            }
            System.out.println(Thread.currentThread().getName() + "_B");
            flag.set(!flag.get());

            // 通知打印A
            wait_print_a_queue.signal();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        PrintABSequenceV3 ins = new PrintABSequenceV3();
        Thread a = new Thread(() -> {
            while (true) {
                ins.printA();
            }
        });
        Thread b = new Thread(() -> {
            while (true) {
                ins.printB();
            }
        });

        a.start();
        b.start();
    }
}
