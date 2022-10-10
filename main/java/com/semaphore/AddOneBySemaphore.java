package com.semaphore;

import java.util.concurrent.Semaphore;

/**
 * @author walker
 * @since 2022/9/19 23:38
 */
public class AddOneBySemaphore {

    static int cnt = 0;

    /**
     * 如果permit为2，且线程数也是2，那么会同时进来临界区
     */
    static Semaphore semaphore = new Semaphore(2, true);

    public static void addOne() throws InterruptedException {
        semaphore.acquire();
        try {
            cnt += 1;
            // 当permits大于1时，会有多个线程进来到临界区，打印出相同的cnt，结果可能有问题
            // current thread id Thread-1, cnt = 2
            // current thread id Thread-0, cnt = 2
            System.out.println("current thread id " + Thread.currentThread().getName() + ", cnt = " + cnt);
        } finally {
            semaphore.release();
        }
    }

    public synchronized static void addOneV1() {
        cnt += 1;
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new AddOneThread();
        Thread t2 = new AddOneThread();

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("res=" + cnt);
    }

    static class AddOneThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    AddOneBySemaphore.addOne();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
