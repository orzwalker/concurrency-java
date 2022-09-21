package com.semaphore;

import com.sun.xml.internal.fastinfoset.tools.FI_SAX_Or_XML_SAX_SAXEvent;

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
        Runnable runnable = () -> {
            String tName = Thread.currentThread().getName();
            System.out.printf("车主:%s 停车场外排队, 时间:%s%n", tName, new Date());
            try {
                // 让所有线程都等待
                Thread.sleep(1000);

                semaphore.acquire();
                // fixme 为什么availablePermits数量至少大于等于0？====可用的许可证最多有N个，最少就0个，不可能是负数？
                 System.out.printf("车主:%s 已进入停车场, 时间:%s, permits:%s%n", tName, new Date(), semaphore.availablePermits());
//                System.out.printf("%s acquire, available permits:%s%n", tName, semaphore.availablePermits());
                Thread.sleep(200);
                 System.out.printf("车主:%s 已离开停车场, 时间:%s%n", tName, new Date());
                semaphore.release();
//                System.out.printf("%s release, available permits:%s%n", tName, semaphore.availablePermits());
            } catch (InterruptedException e) {
                e.printStackTrace();
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
