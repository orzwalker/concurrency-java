package com.base;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 使用阻塞队列实现简易线程池
 * 生产者-消费者 模式
 *
 * @author walker
 * @since 2021/12/20 20:48
 */
public class ThreadPoolDemo {

    BlockingQueue<Runnable> workQueue;
    List<WorkThread> threads = new ArrayList<>();

    /**
     * 初始化线程池
     *
     * @param poolSize  线程池工作线程个数
     * @param workQueue 阻塞队列
     */
    ThreadPoolDemo(int poolSize, BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        for (int i = 0; i < poolSize; i++) {
            WorkThread thread = new WorkThread();
            thread.start();
            this.threads.add(thread);
        }
    }

    void submit(Runnable task) {
        try {
            workQueue.put(task);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 工作线程，负责消费workQueue中的任务
     */
    class WorkThread extends Thread {
        public void run() {
            // 循环取queue中的任务 并执行
            while (true) {
                try {
                    // 如果为空，则阻塞（等待队列非空）
                    Runnable task = workQueue.take();
                    System.out.println("当前线程处理任务:" + Thread.currentThread().getName());
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(3);
        ThreadPoolDemo pool = new ThreadPoolDemo(2, workQueue);
        pool.submit(() -> System.out.println("execute task1 ..."));
        pool.submit(() -> System.out.println("execute task2 ..."));
        pool.submit(() -> System.out.println("execute task3 ..."));
        pool.submit(() -> System.out.println("execute task4 ..."));
    }
}


