package com.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author walker
 * @since 2021/12/21 00:19
 */
public class ThreadPoolDemo3 {
    public static void main(String[] args) {
        ExecutorService executor = new ThreadPoolExecutor(
                2,
                2,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        try {
            Future<?> future1 = executor.submit(() -> System.out.println("task 1 ..." + Thread.currentThread().getName()));
            System.out.println(future1.isDone());
            Future<?> future2 = executor.submit(() -> System.out.println("task 2 ..." + Thread.currentThread().getName()));
            System.out.println(future2.isCancelled());
            Future<?> future3 = executor.submit(() -> System.out.println("task 3 ..." + Thread.currentThread().getName()));
            System.out.println(future3.get());
            Future<?> future4 = executor.submit(() -> System.out.println("task 4 ..." + Thread.currentThread().getName()));
            System.out.println(future4.get(1, TimeUnit.SECONDS));
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

    }
}
