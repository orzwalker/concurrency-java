package com.base;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.function.Function;

/**
 * 信号量实现限流器
 * 一次性创建N个对象，之后每个线程重复利用这N个对象
 *
 * @author walker
 * @since 2021/12/18 23:59
 */
public class SemaphoreRateLimiter<T, R> {

    final List<T> pool;

    final Semaphore semaphore;

    SemaphoreRateLimiter(Integer permits, T t) {
        pool = new Vector<>();
        for (int i = 0; i < permits; i++) {
            pool.add(t);
        }
        semaphore = new Semaphore(permits);
    }

    R exec(Function<T, R> func) {
        T t = null;
        try {
            semaphore.acquire();
            t = pool.remove(0);
            return func.apply(t);
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            pool.add(t);
            semaphore.release();
        }
        return null;
    }

    public static void main(String[] args) {
        SemaphoreRateLimiter<Integer, String> pool = new SemaphoreRateLimiter<>(5, 3);

        for (int i = 0; i < 20; i++) {
            new Thread(() -> pool.exec(t -> {
                System.out.println(t);
                return t.toString();
            })).start();
        }
    }

}
