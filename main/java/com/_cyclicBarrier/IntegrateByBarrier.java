package com._cyclicBarrier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * N个任务计算，M个任务整合计算后的结果
 * M必须等待N结束才能开始
 *
 * @author walker
 * @since 2022/11/13 23:52
 */
public class IntegrateByBarrier {
    public static void main(String[] args) {
        int size = 3;
        List<Integer> res = new ArrayList<>();
        AtomicReference<List<Integer>> list = new AtomicReference<>(res);
        ExecutorService executorService = Executors.newFixedThreadPool(size + 2);

        // 一般由最后一个到达栅栏的线程执行该任务
        CyclicBarrier barrier = new CyclicBarrier(size, new IntegrateRunnable(list));

        executorService.submit(new CalcRunnable(barrier, list, 3, 33));
        executorService.submit(new CalcRunnable(barrier, list, 5, 44));
        executorService.submit(new CalcRunnable(barrier, list, 1, 55));

        executorService.shutdown();
    }
}

/**
 * 计算任务
 */
class CalcRunnable implements Runnable {

    private final CyclicBarrier barrier;
    private final AtomicReference<List<Integer>> list;
    private final int waitTime;
    private final int value;

    public CalcRunnable(CyclicBarrier barrier, AtomicReference<List<Integer>> list, int waitTime, int value) {
        this.barrier = barrier;
        this.list = list;
        this.waitTime = waitTime;
        this.value = value;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + "开始计算" + System.currentTimeMillis());
        try {
            TimeUnit.SECONDS.sleep(waitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        list.get().add(value);
        System.out.println(name + "计算完成" + System.currentTimeMillis());
        // 等待N个任务都完成
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 整合任务
 */
class IntegrateRunnable implements Runnable {

    private final AtomicReference<List<Integer>> list;

    public IntegrateRunnable(AtomicReference<List<Integer>> list) {
        this.list = list;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + "=====计算任务执行结束，开始整合计算结果====" + System.currentTimeMillis());
        list.get().forEach(System.out::println);
    }
}
