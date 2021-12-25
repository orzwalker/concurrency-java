package com.base;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 通过 fork/join框架实现 Fibonacci数
 * f(n) = f(n-1) + f(n-2)，n>=2, f(0)=0, f(1)=1
 *
 * @author walker
 * @since 2021/12/25 13:53
 */
public class ForkJoinDemo1 {
    public static void main(String[] args) {
        // 创建分治任务线程池
        ForkJoinPool forkJoinPool = new ForkJoinPool(3);
        // 创建分治任务
        Fibonacci fibonacci = new Fibonacci(4);
        Integer result = forkJoinPool.invoke(fibonacci);
        System.out.println(result);

        CopyOnWriteArrayList<Integer> cowList = new CopyOnWriteArrayList<>();
        cowList.addAllAbsent(new ArrayList<>());
        cowList.get(1);
    }


    /**
     * @see RecursiveTask，继承了ForkJoinTask，并且compute有返回值
     */
    public static class Fibonacci extends RecursiveTask<Integer> {

        private final int n;

        public Fibonacci(Integer n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                return n;
            }
            Fibonacci f1 = new Fibonacci(n - 2);
            Fibonacci f2 = new Fibonacci(n - 1);
            // 创建子任务
            f2.fork();
            // join阻塞当前线程，直到子任务计算完成，合并子任务结果
            int res = f1.compute() + f2.join();
            System.out.println(res);
            return res;
        }
    }
}


