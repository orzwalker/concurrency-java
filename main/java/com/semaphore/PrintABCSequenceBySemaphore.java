package com.semaphore;

import java.util.concurrent.Semaphore;

/**
 * 使用信号量实现3个线程交替打印ABC
 */
public class PrintABCSequenceBySemaphore {

    static int size = 3;

    static Semaphore a = new Semaphore(1);
    static Semaphore b = new Semaphore(0);
    static Semaphore c = new Semaphore(0);

    public static void main(String[] args) {
        Thread printA = new Thread(new PrintA());
        Thread printB = new Thread(new PrintB());
        Thread printC = new Thread(new PrintC());

        printA.start();
        printB.start();
        printC.start();

    }

    static class PrintA implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < size; i++) {
                try {
                    // a.permits=0，不会阻塞，继续执行
                    a.acquire();
                    System.out.println(Thread.currentThread().getName() + " A");
                    // b.permits=1，打印B的线程能正常执行
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
            for (int i = 0; i < size; i++) {
                try {
                    b.acquire();
                    System.out.println(Thread.currentThread().getName() + " B");
                    c.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    static class PrintC implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < size; i++) {
                try {
                    c.acquire();
                    System.out.println(Thread.currentThread().getName() + " C");
                    a.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
