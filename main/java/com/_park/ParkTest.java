package com._park;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * park、unpark方法成对使用时，前后顺序没有严格限制
 * <p>
 * park、unpark 如何响应中断
 */
public class ParkTest {
    public static void main(String[] args) throws InterruptedException {
//        test1();
//        System.out.println("\n");
//        test2();
//        System.out.println("\n");
//        test3();
//        test4();
        test5();

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
        _sleep(2);
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
            _sleep(2);
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


    public static void test3() throws InterruptedException {
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
        TimeUnit.SECONDS.sleep(3);
        System.out.println("子线程状态：" + thread.getState().name());
        System.out.println("主线程状态：" + Thread.currentThread().getState().name());

        /**
         * 连续两次unpark，但是permit最大值是1
         * 所以后边两次park获取许可时，第二次失败，一直阻塞
         *
         * start
         * unpart 1
         * unpart 2
         * park 1
         * stateName:RUNNABLE ordinal:1
         * park 2
         * 子线程状态：WAITING
         * 主线程状态：RUNNABLE
         */
    }

    public static void test4() {
        Thread.currentThread().interrupt();
        LockSupport.park();
        System.out.println("1 先interrupt，后park");
        // 程序正常执行结束，不阻塞====因为interrupt中会调用unpark方法

        LockSupport.park();
        System.out.println("2 先interrupt，后park");
        LockSupport.park();
        System.out.println("3 先interrupt，后park");
        // 2、3也能正常执行，因为线程处于中断状态，park时直接返回

    }

    public static void test5() {
        Thread main = Thread.currentThread();
        Thread thread = new Thread(() -> {
            System.out.println("子线程sleep 2s");
            _sleep(2);
            System.out.println("main线程是否中断: " + main.isInterrupted()); // false
            System.out.println("main线程状态: " + main.getState().name()); // WAITING
            System.out.println("中断main线程");
            main.interrupt();
            System.out.println("main线程是否中断: " + main.isInterrupted()); // true
            _sleep(2);
            System.out.println("main线程状态: " + main.getState().name()); // TERMINATED
        });
        thread.start();
        LockSupport.park();
    }


    private static void threadInfo(Thread thread) {
        System.out.println("stateName:" + thread.getState().name() + " ordinal:" + thread.getState().ordinal());
    }

    private static void _sleep(int time) {
        if (time > 0) {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
