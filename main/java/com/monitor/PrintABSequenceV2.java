package com.monitor;

/**
 * 双线程顺序打印A、B
 */
public class PrintABSequenceV2 {

    volatile boolean flag;

    final Object lock = new Object();

    public void printA() {
        synchronized (lock) {
            while (flag) {
                try {
                    // 释放锁资源
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "_A");
            flag = !flag;
            lock.notify();
        }
    }

    public void printB() {
        synchronized (lock) {
            while (!flag) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName() + "_B");
            flag = !flag;
            lock.notify();
        }
    }

    public static void main(String[] args) {
        PrintABSequenceV2 ins = new PrintABSequenceV2();
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
