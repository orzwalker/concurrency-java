package com.monitor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author walker
 * @since 2024/3/27 19:16
 */
public class TestCondition {

    private static ReentrantLock lock = new ReentrantLock();
    static Condition aCondition = lock.newCondition();
    static Condition bCondition = lock.newCondition();
    static Condition cCondition = lock.newCondition();
    /**
     * 1 打印a
     * 2 打印b
     * 3 打印c
     */
    static AtomicInteger isPrintedA = new AtomicInteger(1);

    public static void main(String[] args) throws InterruptedException {
        // for (int i = 0; i < 20; i++) {
        while (true){
            new Thread(() -> {
                lock.lock();
                try {
                    while (isPrintedA.get() != 1) {
                        aCondition.await();
                    }
                    System.out.println("A");
                    isPrintedA.set(2);
                    bCondition.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();

            new Thread(() -> {
                lock.lock();
                try {
                    while (isPrintedA.get() != 2) {
                        bCondition.await();
                    }
                    System.out.println("B");
                    isPrintedA.set(3);
                    cCondition.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();

            new Thread(() -> {
                lock.lock();
                try {
                    while (isPrintedA.get() != 3) {
                        bCondition.await();
                    }
                    System.out.println("C");
                    isPrintedA.set(1);
                    aCondition.signal();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }).start();
        }
    }
}


