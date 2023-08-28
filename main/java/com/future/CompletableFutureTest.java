package com.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2023/8/22 23:39
 */
public class CompletableFutureTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName() + "aaa";
        }, executorService);

        CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName() + "bbb";
        }, executorService);

        CompletableFuture<String> c = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName() + "ccc";
        }, executorService);


        CompletableFuture<String> firstCompletedFuture = CompletableFuture.anyOf(a, b, c).thenApplyAsync(result -> (String) result, executorService);
        System.out.println(firstCompletedFuture.get());

//
//        String A = a.get();
//        String B = b.get();
//        String C = c.get();
//
//        System.out.println("A:" + A);
//        System.out.println("B:" + B);
//        System.out.println("C:" + C);

        executorService.shutdown();

    }
}
