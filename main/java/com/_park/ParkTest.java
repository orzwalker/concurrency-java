package com._park;

import java.util.concurrent.locks.LockSupport;

/**
 * park、unpark方法成对使用时，前后顺序没有严格限制
 */
public class ParkTest {
    public static void main(String[] args) {
        test1();
        System.out.println("\n");
        test2();
        System.out.println("\n");
        test3();

    }

    public static void test1() {
        System.out.println("start");
        Thread thread = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " start park");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + " end park");
        }, "t1");
        thread.start();

        // 子线程阻塞2秒，后被主线程unpark唤起
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        threadInfo(thread);

        System.out.println(Thread.currentThread().getName() + " start unpark");
        LockSupport.unpark(thread);
        System.out.println(Thread.currentThread().getName() + " end unpark");

        /**
         * 执行park后，没有permit，所以阻塞
         * 主线程给一个permit后，park的线程被唤起
         *
         * start
         * t1 start park
         * stateName:WAITING ordinal:3
         * main start unpark
         * main end unpark
         * t1 end park
         */
    }


    public static void test2() {
        System.out.println("start");
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " start park");
            LockSupport.park();
            threadInfo(Thread.currentThread());
            System.out.println(Thread.currentThread().getName() + " end park");
        }, "t1");

        thread.start();

        System.out.println(Thread.currentThread().getName() + " start unpark");
        LockSupport.unpark(thread);
        System.out.println(Thread.currentThread().getName() + " end unpark");

        /**
         * 先unpark给1个许可permit，这样后边执行park不会阻塞，直接执行
         *
         * start
         * main start unpark
         * main end unpark
         * t1 start park
         * stateName:RUNNABLE ordinal:1
         * t1 end park
         */
    }


    public static void test3() {
        System.out.println("start");
        Thread thread = new Thread(() -> {
            Thread t = Thread.currentThread();
            System.out.println("unpart 1");
            LockSupport.unpark(t);
            System.out.println("unpart 2");
            LockSupport.unpark(t);

            System.out.println("park 1");
            LockSupport.park();
            threadInfo(t);
            System.out.println("park 2");
            LockSupport.park();
            threadInfo(t);

            System.out.println("park end");
        }, "t1");

        thread.start();

        /**
         * 连续两次park，但是permit最大值是1
         * 所以后边两次park获取许可时，第二次失败，一直阻塞
         *
         * start
         * unpart 1
         * unpart 2
         * park 1
         * stateName:RUNNABLE ordinal:1
         * park 2
         */
    }


    private static void threadInfo(Thread thread) {
        System.out.println("stateName:" + thread.getState().name() + " ordinal:" + thread.getState().ordinal());
    }
}
