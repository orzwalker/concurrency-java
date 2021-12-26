package com.base;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CompletableFutureDemo {
    public static void main(String[] args) {
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("T1:洗水壶...");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("T1:烧开水...");
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<Void> f2 = CompletableFuture.runAsync(() -> {
            try {
                System.out.println("T2:洗茶壶...");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("T2:洗茶杯...");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("T2:拿茶叶...");
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

}
