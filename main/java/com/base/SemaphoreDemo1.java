package com.base;

import java.util.concurrent.Semaphore;

/**
 * 信号量实现管程
 *
 * @author walker
 * @since 2021/12/17 16:03
 */
public class SemaphoreDemo1 {

    private static int cnt = 0;

    private final static Semaphore semaphore = new Semaphore(1);

    void addOne() {
        try {
            semaphore.acquire();
            cnt += 1;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
        }
        System.out.println(cnt);
    }

    void subOne() {
        synchronized (this) {
            cnt += 1;
            System.out.println(cnt);
        }
    }

    public static void main(String[] args) {
        SemaphoreDemo1 ins = new SemaphoreDemo1();
        int size = 100;
        while (size > 0) {
            Thread thread = new Thread(ins::addOne);
            thread.start();
            size--;
        }
    }
}
