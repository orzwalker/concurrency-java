package com.monitor;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用管程实现阻塞队列
 * 当队列空时，出队列任务进入等待队列
 * 当队列满时，入队列任务进入等待队列
 * 当执行完入队列操作后，阻塞队列非空，唤起出任务的等待队列
 * 当执行完出队列操作后，阻塞队列有空间，唤起入任务的等待队列
 * 堵塞队列当前元素数目方法size线程安全
 *
 * @author walker
 * @since 2022/9/4 23:12
 */
public class BlockQueueWithMonitorV2 {
    /**
     * 使用管程模型进行分析：
     * 1、共享变量：阻塞队列
     * 2、条件变量有两个，分别是队列空、队列满
     * 3、对应的条件等待队列也是两个，分别为队列为空、队列满
     */

    final Lock lock = new ReentrantLock();
    /**
     * 队列空
     */
    final Condition queue_empty = lock.newCondition();
    /**
     * 队列满
     */
    final Condition queue_full = lock.newCondition();


    private Integer CAPACITY;
    private volatile Queue<String> queue = new LinkedList<>();

    public BlockQueueWithMonitorV2(int size) {
        if (size < 0) {
            CAPACITY = 1;
        } else {
            this.CAPACITY = size;
        }
    }

    /**
     * 入队列
     */
    public void enq(String ele) {
        lock.lock();
        try {
            // 队列满
            while (size() >= CAPACITY) {
                queue_full.await();
            }

            // 队列不满，执行入操作，并唤起"队列空"这个等待队列中的任务
            System.out.println(Thread.currentThread().getName() + " 入队元素:" + ele);
            queue.add(ele);
            queue_empty.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队列
     */
    synchronized public void dep() {
        lock.lock();
        try {
            // 队列空
            while (size() <= 0) {
                queue_empty.await();
            }

            // 队列非空，执行出队列操作，并唤起"队列满"这个等待队列中的任务
            String res = queue.poll();
            System.out.println(Thread.currentThread().getName() + " 出队元素:" + res);
            queue_full.signalAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }


    public Integer size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) {
        BlockQueueWithMonitorV2 queue = new BlockQueueWithMonitorV2(2);
        queue.enq("1");
        queue.enq("2");
        queue.dep();
        queue.enq("3");
        queue.dep();
        queue.dep();
    }
}

