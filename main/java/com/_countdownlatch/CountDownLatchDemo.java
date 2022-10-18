package com._countdownlatch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2022/10/18 10:55
 */
public class CountDownLatchDemo {

    public static void main(String[] args) {

        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                System.out.println("t1 sleep ex:" + e.getMessage());
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        });

        Thread t2 = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("t2 sleep ex:" + e.getMessage());
                Thread.currentThread().interrupt();
            }
            latch.countDown();
        });

        Thread t3 = new Thread(() -> {
            try {
                latch.await();
                System.out.println("t3 从await中返回了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread t4 = new Thread(() -> {
            try {
                latch.await();
                System.out.println("t4 从await中返回了");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        /**
         * t3 从await中返回了
         * t4 从await中返回了
         *
         * ==== 两个输出顺序不是绝对的
         */
    }
}
