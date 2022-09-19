package com.semaphore;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 停车场限流
 *
 * @author walker
 * @since 2022/9/20 00:52
 */
public class ParkingLimitBySemaphore {

    /**
     * 停车场最多能停3辆车
     */
    static Semaphore semaphore = new Semaphore(3);

    /**
     * 模拟10辆车同时进来停车
     */
    static ExecutorService pool = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String tName = Thread.currentThread().getName();
                System.out.printf("车主:%s 停车场外排队, 时间:%s%n", tName, new Date());
                try {
                    // 让所有线程都等待
                    Thread.sleep(1000);

                    semaphore.acquire();
                    System.out.printf("车主:%s 已进入停车场, 时间:%s%n", tName, new Date());
                    Thread.sleep(1000);
                    System.out.printf("车主:%s 已离开停车场, 时间:%s%n", tName, new Date());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }
        };

        // 同时停车
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);
        pool.submit(runnable);

        pool.shutdown();

    }
}
