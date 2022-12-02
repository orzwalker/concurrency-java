package com.threadPool._schedule;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 什么场景下适合使用{@link ScheduledThreadPoolExecutor}线程池
 *
 * @author walker
 * @since 2022/12/1 00:32
 */
public class ScheduleThreadPoolExecutorTest {
    @SneakyThrows
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(2);


        /**
         * {@link ScheduledThreadPoolExecutor#schedule(Runnable, long, TimeUnit)}
         * Runnable抛出异常时，内部消化
         */
        threadPoolExecutor.schedule(() -> System.out.println(Thread.currentThread().getName() + " i=" + 1 / 0), 0, TimeUnit.SECONDS);
        threadPoolExecutor.schedule(() -> System.out.println(Thread.currentThread().getName() + " execute task 1 after 2s"), 2, TimeUnit.SECONDS);
        ScheduledFuture<?> scheduledFuture = threadPoolExecutor.schedule(() -> System.out.println(Thread.currentThread().getName() + " execute task 2 after 3s"), 3, TimeUnit.SECONDS);
        // 拿到返回值后可以取消任务
        scheduledFuture.cancel(true);

        /**
         * Callable会向上抛出异常，main线程响应中断，程序不会继续执行
         */
        Callable<Integer> callable = () -> {
            System.out.println("callable");
            return 1 / 0;
        };
        ScheduledFuture<Integer> res = threadPoolExecutor.schedule(callable, 1, TimeUnit.SECONDS);
        System.out.println("callable res:" + res.get());

        /**
         * 定时任务抛出异常时，会中断，不再执行
         * {@link java.util.concurrent.FutureTask#runAndReset}
         * 不会抛出异常，需要注意
         */
        AtomicInteger num = new AtomicInteger(0);
        Runnable runnable = () -> {
            try {
                System.out.println(num.getAndIncrement());
                if (num.get() > 3) {
                    throw new RuntimeException("定时任务中断异常"); // 异常不会抛出
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
        threadPoolExecutor.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.SECONDS);


        System.out.println(Thread.currentThread().getName() + " ====>OK");
        // threadPoolExecutor.shutdown();
    }
}
