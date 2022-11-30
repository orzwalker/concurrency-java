package com.threadPool._schedule;

import lombok.SneakyThrows;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2022/12/1 00:32
 */
public class ScheduleThreadPoolExecutorTest {
    @SneakyThrows
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(2);
        threadPoolExecutor.schedule(() -> System.out.println(Thread.currentThread().getName() + " execute task 1 after 2s"), 2, TimeUnit.SECONDS);
        ScheduledFuture<?> scheduledFuture = threadPoolExecutor.schedule(() -> System.out.println(Thread.currentThread().getName() + " execute task 2 after 3s"), 3, TimeUnit.SECONDS);
        // 取消任务
//        scheduledFuture.cancel(true);

        System.out.println(Thread.currentThread().getName() + " ====>OK");
        threadPoolExecutor.shutdown();
    }
}
