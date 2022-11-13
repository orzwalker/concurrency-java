package com._cyclicBarrier;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 等待N的任务执行结束后，统一执行某件事
 *
 * @author walker
 * @since 2022/11/13 23:29
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties, () -> System.out.println("====任务到齐，开始执行===" + System.currentTimeMillis()));
        ExecutorService executorService = Executors.newFixedThreadPool(parties);
        executorService.execute(new Worker(barrier, "A", 5));
        executorService.execute(new Worker(barrier, "B", 2));
        executorService.execute(new Worker(barrier, "C", 3));

        executorService.shutdown();
    }
}


class Worker implements Runnable {
    private final CyclicBarrier barrier;
    private final String name;
    private final int waitTime;

    public Worker(CyclicBarrier barrier, String name, int waitTime) {
        this.barrier = barrier;
        this.name = name;
        this.waitTime = waitTime;
    }

    @Override
    public void run() {
        System.out.println(name + "准备去往栅栏" + System.currentTimeMillis());
        try {
            TimeUnit.SECONDS.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(name + "到达栅栏" + System.currentTimeMillis());
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
        System.out.println(name + "穿过栅栏" + System.currentTimeMillis());
    }
}
