package com.monitor;

/**
 * 多线程顺序打印A、B
 * 1、存在一个共享变量
 * 2、这个共享变量在多线程间的可见性，且需要禁用缓存
 * 3、根据这个共享变量的不同状态，决定执行哪个线程
 * 4、线程间需要互相通信
 */
public class PrintABSequence {

    static boolean flag;

    public static void main(String[] args) {
        Object lock = new Object();
        Thread a = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    // 执行wait()的线程，如果重新被唤醒，是从wait()代码之后继续执行的，而不是重新从该方法的头部重新执行
                    // pc（程序计数器）
                    // 因此需要while，再次判断是否满足条件
                    while (flag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + "--A");
                    flag = !flag;
                    lock.notify();
                }
            }
        });
        Thread b = new Thread(() -> {
            while (true) {
                synchronized (lock) {
                    while (!flag) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(Thread.currentThread().getName() + "--B");
                    flag = !flag;
                    lock.notify();
                }
            }
        });
        a.start();
        b.start();

    }
}
