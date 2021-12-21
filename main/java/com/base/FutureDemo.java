package com.base;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 获取线程执行结果
 *
 * @author walker
 * @since 2021/12/21 13:31
 */
public class FutureDemo {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        /**
         * 实现了Runnable接口，所以可以把FutureTask交给ThreadPoolExecutor执行，也可以被Thread直接执行
         * 实现了Future接口，所以可以获取到任务执行结果
         */
        FutureTask<Integer> futureTask = new FutureTask<>(() -> 1 + 2);

        // 交给ExecutorService执行
        executorService.submit(futureTask);
        System.out.println(futureTask.get());
        executorService.shutdown();

        // 交给Thread执行
        Thread thread = new Thread(futureTask);
        thread.start();
        System.out.println(futureTask.get());
    }
}
