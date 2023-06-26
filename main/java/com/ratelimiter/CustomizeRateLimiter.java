package com.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;
import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2023/6/25 17:47
 */
public class CustomizeRateLimiter {

    private RateLimiter rateLimiter = null;

    public CustomizeRateLimiter(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    /**
     * 1、令牌桶每秒创建一个令牌放到桶中
     * 2、流量访问时每次需要两个令牌
     * 所以每2秒执行一次业务请求，拿不到令牌的请求被阻塞${@link RateLimiter#acquire()}
     */
    public void testAcquire() {
        rateLimiter = RateLimiter.create(1);
        while (true) {
            System.out.println("A-" + rateLimiter.acquire(2));
            System.out.println("B-" + rateLimiter.acquire(2));
        }
    }


    /**
     * 所以每2秒执行一次业务请求，拿不到令牌的请求被丢弃${@link RateLimiter#tryAcquire()} ----非阻塞
     *
     * A-true
     * B-false
     * A-false
     * B-false
     * A-true
     * B-false
     * A-false
     * B-false
     */
    @SneakyThrows
    public void testTryAcquire() {
        rateLimiter = RateLimiter.create(1);
        while (true) {
            System.out.println("A-" + rateLimiter.tryAcquire(2));
            System.out.println("B-" + rateLimiter.tryAcquire(2));
            TimeUnit.SECONDS.sleep(1);
        }
    }


    public static void main(String[] args) {
        CustomizeRateLimiter ins = new CustomizeRateLimiter(RateLimiter.create(1));
//        ins.testAcquire();
        ins.testTryAcquire();
    }
}
