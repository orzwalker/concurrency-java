package com.base;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 实现烧水泡茶程序
 *
 * @author walker
 * @since 2021/12/21 13:46
 */
public class FutureTaskDemo {

    public static void main(String[] args) {
        FutureTaskDemo ins = new FutureTaskDemo();
        ins.exec();
    }

    void exec() {
        FutureTask<String> ft2 = new FutureTask<>(new ThreadTaskTwo());
        FutureTask<String> ft1 = new FutureTask<>(new ThreadTaskOne(ft2));

        new Thread(ft1).start();
        new Thread(ft2).start();
        try {
            System.out.println(ft1.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    static class ThreadTaskOne implements Callable<String> {
        FutureTask<String> ft2;

        ThreadTaskOne(FutureTask<String> ft2) {
            this.ft2 = ft2;
        }

        @Override
        public String call() throws Exception {
            try {
                System.out.println("T1 洗水壶");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("T1 烧开水");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("T1 等待 T2执行结束...");
                String s = ft2.get();
                System.out.println("T1 泡茶，+" +s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "饮茶...";
        }
    }


    static class ThreadTaskTwo implements Callable<String> {
        @Override
        public String call() throws Exception {
            System.out.println("T2 洗茶壶");
            TimeUnit.SECONDS.sleep(1);
            System.out.println("T2 洗茶杯");
            TimeUnit.SECONDS.sleep(2);
            System.out.println("T2 拿茶叶");
            return "白茶";
        }
    }
}
