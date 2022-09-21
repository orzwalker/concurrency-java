package com.semaphore;

import java.util.concurrent.Semaphore;

/**
 * 使用信号量实现双线程交替打印AB
 */
public class PrintABSequenceBySemaphore {

    static Semaphore a = new Semaphore(1);
    static Semaphore b = new Semaphore(0);

    public static void main(String[] args) {
        PrintABSequenceBySemaphore ins = new PrintABSequenceBySemaphore();
        Thread printA = new Thread(new PrintA());
        Thread printB = new Thread(new PrintB());

        printA.start();
        printB.start();

    }

    static class PrintA implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                try {
                    // a.permits=0，不会阻塞，继续执行
                    a.acquire();
                    System.out.println(Thread.currentThread().getName() + " A");
                    b.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class PrintB implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 5; i++) {
                try {
                    b.acquire();
                    System.out.println(Thread.currentThread().getName() + " B");
                    a.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
