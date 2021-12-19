package com.base;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁降级
 *
 * @author walker
 * @since 2021/12/19 22:02
 */
public class ReadWriteLockDemo2 {

    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    Lock readLock = readWriteLock.readLock();
    Lock writeLock = readWriteLock.writeLock();

    /**
     * 锁降级前，把读写两个线程放入等待队列中
     */
    void processReadThread(int threadIndex) {
        new Thread(() -> {
            System.out.println("线程" + threadIndex + "开始抢读锁，阻塞中");
            readLock.lock();
            System.out.println("线程" + threadIndex + "抢到读锁，执行...");
            readLock.unlock();
            System.out.println("线程" + threadIndex + "释放读锁");
        }).start();
    }

    /**
     * 锁降级前，把读写两个线程放入等待队列中
     */
    void processWriteThread(int threadIndex) {
        new Thread(() -> {
            System.out.println("线程" + threadIndex + "开始抢写锁，阻塞中");
            writeLock.lock();
            System.out.println("线程" + threadIndex + "抢到写锁，执行...");
            writeLock.unlock();
            System.out.println("线程" + threadIndex + "释放写锁");
        }).start();
    }

    /**
     * 试验重入锁
     */
    void query() {
        readLock.lock();
        try {
            System.out.println("查询数据成功...");
        } finally {
            readLock.unlock();
        }
    }

    void test() {
        System.out.println("主线程开始...");
        writeLock.lock();
        System.out.println("主线程抢到写锁");

        try {
            processReadThread(1);
            processWriteThread(2);
            // 主线程休眠，让以上2个方法的 两个子线程进入到等待队列中(主线程持有写锁，其他任何线程都会被阻塞)
            Thread.sleep(500);

            System.out.println("主线程处理写操作");
            // 降级锁，写锁释放前操作
            readLock.lock();
            System.out.println("降级开始，主线程获取到读锁");
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            writeLock.unlock();
            System.out.println("主线程释放写锁");
        }
        try {
            System.out.println("主线程处理读操作");
            // 判断是否是重入锁
            query();
        } finally {
            readLock.unlock();
            System.out.println("主线程释放读锁");
        }
    }


    public static void main(String[] args) {
        ReadWriteLockDemo2 ins = new ReadWriteLockDemo2();
        ins.test();
    }
}
