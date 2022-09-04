package com.monitor;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 使用管程实现线程安全的阻塞队列
 *
 * @author walker
 * @since 2022/9/4 11:12
 */
public class BlockQueueWithMonitor {

    private Integer CAPACITY;
    private volatile Queue<String> queue = new LinkedList<>();

    public BlockQueueWithMonitor(int size) {
        if (size < 0) {
            CAPACITY = 1;
        } else {
            this.CAPACITY = size;
        }
    }

    /**
     * 入队列
     */
    synchronized public void enq(String ele) {
        while (size() >= CAPACITY) {
            try {
                System.out.println(Thread.currentThread().getName() + " 当前队列已满，size:" + size());
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }
        System.out.println(Thread.currentThread().getName() + " 入队元素:" + ele);
        queue.add(ele);
    }

    /**
     * 出队列
     */
    synchronized public String dep() {
        String res = null;
        if (size() > 0) {
            res = queue.poll();
            System.out.println(Thread.currentThread().getName() + " 出队元素:" + res);
            // 唤起等待队列，继续入操作
            notifyAll();
        }
        return res;
    }


    synchronized public Integer size() {
        return queue.size();
    }


    public static void main(String[] args) {
        BlockQueueWithMonitor queue = new BlockQueueWithMonitor(2);
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            new Thread(() -> {
                if (queue.size() < queue.CAPACITY) {
                    queue.enq("_" + finalI);
                } else {
                    queue.dep();
                }
            }).start();
        }
//        System.out.println(Thread.currentThread().getName() + " -" + queue.dep());
        /**
         * Thread-0 入队元素:_0
         * Thread-4 入队元素:_4
         * Thread-3 出队元素:_0
         * Thread-2 入队元素:_2
         * Thread-1 当前队列已满，size:2
         */


//        for (int i = 0; i < 5; i++) {
//            if (queue.size() < queue.CAPACITY) {
//                queue.enq("==" + i);
//            } else {
//                queue.dep();
//            }
//        }
        /**
         * main 入队元素:==0
         * main 入队元素:==1
         * main 出队元素:==0
         * main 入队元素:==3
         * main 出队元素:==1
         */
    }
}

