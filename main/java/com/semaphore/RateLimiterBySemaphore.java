package com.semaphore;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author walker
 * @since 2022/9/20 00:23
 */
public class RateLimiterBySemaphore<T, R> {

    List<T> pool;
    static Semaphore SEMAPHORE;

    public RateLimiterBySemaphore(int permits, T t) {
        pool = new Vector<>(permits); // 池子大小，一次性最多能拿多少个资源
        SEMAPHORE = new Semaphore(permits);
        for (int i = 0; i < permits; i++) {
            pool.add(t); // 可以复用的对象
        }
    }

    R execute(Function<T, R> function) throws InterruptedException {
        T t = null;
        SEMAPHORE.acquire();
        try {
            System.out.println("curr thread:" + Thread.currentThread().getName() +
                    " availablePermits=" + SEMAPHORE.availablePermits() +
                    " drainPermits=" + SEMAPHORE.drainPermits() +
                    " queue length = " + SEMAPHORE.getQueueLength());
            t = pool.remove(0);
            if (null != t) {
                return function.apply(t);
            }
        } finally {
            pool.add(t);
            SEMAPHORE.release();
        }
        return null;
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(20);
        RateLimiterBySemaphore semaphore = new RateLimiterBySemaphore(1, "xxx");

        Runnable runnable = () -> {
            try {
                Thread.sleep(1000);

                semaphore.execute(r -> r);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        for (int i = 0; i < 20; i++) {
            pool.submit(runnable);
        }

        pool.shutdown();

    }
}
