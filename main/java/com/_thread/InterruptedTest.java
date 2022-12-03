package com._thread;

import java.util.concurrent.TimeUnit;

/**
 * @author walker
 * @since 2022/10/14 11:53
 */
public class InterruptedTest {

    public static void main(String[] args) {

        Thread thread = new Thread(() -> {
            // while (!Thread.currentThread().isInterrupted()) {

            // 第一次调用interrupted后，线程是中断状态，并清除中断标识
            // 也就是说第二次调用时，处于非中断状态
            while (!Thread.interrupted()) {
                System.out.println("do someThing...");
                System.out.println("准备做下一件事了，如果没有其他线程中断我的话,"
                        + Thread.currentThread().getState().name() + ", "
                        + Thread.currentThread().isInterrupted());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    // 抛出异常时，会清除中断标识
                    // 如果要退出循环，应该重新设置中断标识
                    e.printStackTrace();
                }
            }
        }, "test");
        thread.start();

        System.out.println("主线程中断子线程");
        thread.interrupt();
    }
}
