package com.base;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2021/12/20 23:57
 */
public class ThreadPoolDemo2 {

    public static void main(String[] args) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(3);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,
                2,
                60,
                TimeUnit.SECONDS,
                workQueue,
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        executor.execute(() -> System.out.println("task 1 ..." + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("task 2 ..." + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("task 3 ..." + Thread.currentThread().getName()));
        executor.execute(() -> System.out.println("task 4 ..." + Thread.currentThread().getName()));
    }


}
