package com.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author walker
 * @since 2023/6/26 09:43
 */
public class RateLimiterExample {

    private final RateLimiter rateLimiter;
    private final AtomicInteger blockedThreadCount;

    public RateLimiterExample(RateLimiter rateLimiter, AtomicInteger blockedThreadCount) {
        this.rateLimiter = rateLimiter;
        this.blockedThreadCount = blockedThreadCount;
    }

    /**
     * 获取因为没有拿到令牌而被阻塞的线程数
     * 只能获取到${@link RateLimiter#acquire()}方法中由于没有令牌而被阻塞的线程个数，不能获取到由于其他原因（锁、IO等）而阻塞的线程
     */
    public void test() {
        // 非阻塞
        if (this.rateLimiter.tryAcquire()) {
            System.out.println(Thread.currentThread().getName() + "获取到令牌");
        } else {
            System.out.println(Thread.currentThread().getName() + " 没有拿到令牌，阻塞当前线程，当前被阻塞的线程数" + blockedThreadCount.incrementAndGet());
            try {
                // 阻塞当前线程
                double acquire = this.rateLimiter.acquire();
                System.out.println(Thread.currentThread().getName() + "阻塞线程被唤醒，拿到令牌，阻塞时间" + acquire);
            } finally {
                blockedThreadCount.decrementAndGet();
                System.out.println(Thread.currentThread().getName() + "阻塞线程拿到令牌了，当前被阻塞的线程数" + blockedThreadCount.get());
            }
        }
    }

    public static void main(String[] args) {
        RateLimiterExample ins = new RateLimiterExample(RateLimiter.create(1), new AtomicInteger());
        for (int i = 0; i < 3; i++) {
            new Thread(ins::test).start();
        }
    }

    /**
     * Thread-0获取到令牌
     * Thread-1 没有拿到令牌，阻塞当前线程，当前被阻塞的线程数1
     * Thread-2 没有拿到令牌，阻塞当前线程，当前被阻塞的线程数2
     * Thread-1阻塞线程被唤醒，拿到令牌，阻塞时间0.896504
     * Thread-1阻塞线程拿到令牌了，当前被阻塞的线程数1
     * Thread-2阻塞线程被唤醒，拿到令牌，阻塞时间1.896422
     * Thread-2阻塞线程拿到令牌了，当前被阻塞的线程数0
     */
}
