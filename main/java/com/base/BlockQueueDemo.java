package com.base;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现一个阻塞队列，保证线程安全
 * MESA模型，管程中有共享变量、操作方法、条件变量队列和管程外的入口等待队列
 * 条件变量队列中线程如果需要执行，还需要走一遍入口等待队列
 *
 * @author walker
 * @since 2021/12/13 17:16
 */
public class BlockQueueDemo<T> {
    // 非公平锁
    final Lock lock = new ReentrantLock();

    // 队列没满
    final Condition notFull = lock.newCondition();

    // 队列非空
    final Condition notEmpty = lock.newCondition();

    Queue<T> queue = new LinkedList<>();

    final int capacity = 5;


    /**
     * 入队列
     */
    void enq(T x) {
        lock.lock();
        try {
            // 使用while该值出队变化
            while (queue.size() > capacity) {
                // 队列已满，进入条件等待队列
                notFull.await();
            }
            // 入队
            queue.add(x);
            // 入队后队列非空，满足出队条件，通知「队列非空」条件队列，可以出队
            // while的条件一直没有变化过，所以可以直接使用notify，不需要notifyAll
            notEmpty.signal();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    /**
     * 出队列
     */
    void deq() {
        lock.lock();
        try {
            // 使用while感知入队变化
            while (queue.size() == 0) {
                // 队列为空，线程进条件队列等待
                notEmpty.await();
            }
            // 出队
            queue.poll();
            // 出队后队列有空间入队，通知「队列没满」条件队列，可以入队
            // while的条件一直没有变化过，所以可以直接使用notify，不需要notifyAll
            notFull.signal();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            lock.unlock();
        }

    }

}
