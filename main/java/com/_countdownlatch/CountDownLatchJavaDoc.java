package com._countdownlatch;

import java.util.concurrent.CountDownLatch;

/**
 * 实现一个栅栏，所有的线程都满足这个条件后统一开始执行
 * 执行后将结果反馈出来
 *
 * @author walker
 * @since 2022/10/17 21:51
 */
public class CountDownLatchJavaDoc {

    public static final int N = 5;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(N);

        for (int i = 0; i < N; i++) {
            new Thread(new Work(startSignal, doneSignal)).start();
        }
        // 所有任务开启后统一执行
        startSignal.countDown();
        System.out.println("主线程执行");
        doneSignal.await();
    }


    static class Work implements Runnable {

        private CountDownLatch startSignal;
        private CountDownLatch doneSignal;

        public Work(CountDownLatch startSignal, CountDownLatch doneSignal) {
            this.startSignal = startSignal;
            this.doneSignal = doneSignal;
        }

        @Override
        public void run() {
            try {
                // 等待所有任务就绪
                startSignal.await();
                Thread thread = Thread.currentThread();
                System.out.println(System.currentTimeMillis() + " = " + thread.getName() + "开始执行");
                // count down
                doneSignal.countDown();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
